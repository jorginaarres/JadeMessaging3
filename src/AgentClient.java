import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentClient extends Agent {
    private String CustomerRestaurantWantedType;
    private AID[] sellerAgents ;

    @Override
    protected void setup() {

        System.out.println("Hallo! Buyer-agent "+ getAID().getName() +" is ready.");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            CustomerRestaurantWantedType = "Spanish";
            System.out.println("Trying to buy " + args[0]);
            addBehaviour(new TickerBehaviour(this, 60000) {

                protected void onTick() {
                    // Update the list of agents
                    System.out.println("Ticker behaviour:");
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("restaurant-to-reserve");
                    template.addServices(sd);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        System.out.println("Found the following seller agents:");
                        sellerAgents = new AID[result.length];
                        System.out.println("AgentsNames:");
                        for (int i = 0; i < result.length; ++i) {
                            sellerAgents[i] = result[i].getName();
                            System.out.println(result[i].getName());

                        }
                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }
                    // Perform the request
                    myAgent.addBehaviour(new RequestPerformer());
                }
            });
        } else{
            System.out.println("No type of food specified");
            doDelete();
        }

    }

    @Override
    protected void takeDown() {
        System.out.println("Client-agent " + getAID().getName() + "terminating.");
    }

    class RequestPerformer extends Behaviour {
        private boolean first = true;
        private AID bestrestaurant;
        private String lastsametype;
        // The counter of replies from seller agents
        private MessageTemplate mt;
        private int repliesCnt = 0;

        private int step=0;

        @Override
        public void action() {
            switch(step){
                case 0: // Send the cfp to all restaurants
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    System.out.println("Seller agents: " + sellerAgents);
                    for(int i=0;i< sellerAgents.length;++i){
                        cfp.addReceiver(sellerAgents[i]);
                    }
                    cfp.setContent(CustomerRestaurantWantedType);
                    cfp.setConversationId("restaurant-to-reserve");
                    cfp.setReplyWith("cpf"+ System.currentTimeMillis());

                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("restaurant-to-reserve"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    System.out.println("Case 0. Done");

                    break;
                case 1: // Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    System.out.println("Case 1. Start");

                    if (reply != null) { // Reply received
                        System.out.println("Case 1. This is an offer.0");

                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // This is an offer
                            System.out.println("Case 1. These are the spanish restaurants"+ reply.getContent());

                            String type = reply.getContent();
                            if (bestrestaurant == null ) {
                                // This is the best offer at present
                                lastsametype = type;
                                bestrestaurant = reply.getSender();
                                System.out.println("Case 1. Best offer for the moment"+ bestrestaurant);
                            } }
                        repliesCnt++;
                        if (repliesCnt >= sellerAgents.length) {
                            // We received all replies
                            step = 2; }
                    } else {
                        block(); }
                    break;
                case 2: // Send the purchase order to the seller
                    // that provided the best offer
                    System.out.println("Case 2. Start");

                    ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    order.addReceiver(bestrestaurant);
                    order.setContent(CustomerRestaurantWantedType);
                    order.setConversationId("restaurant-to-reserve");
                    order.setReplyWith("order" + System.currentTimeMillis());
                    myAgent.send(order);

                    MessageTemplate.and(MessageTemplate.MatchConversationId("restaurant-to-reserve"),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                    step = 3;
                    break;
                case 3: // Receive the purchase order reply
                    System.out.println("Case 3. Start");

                    reply = myAgent.receive(mt);
                    if (reply != null) { // Purchase order reply received
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // Purchase successful. We can terminate
                            System.out.println(lastsametype + "successfully purchased.");
                            System.out.println("Type " + bestrestaurant);
                            myAgent.doDelete();
                        }
                        step = 4;
                    } else {
                        block(); }
                    break;

            }
/*            ACLMessage proposal = receive();
            if (proposal!=null && proposal.getPerformative()==ACLMessage.PROPOSE) {
                ACLMessage accept = proposal.createReply();
                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                System.out.println("Client Agent: Yes, I want");
            }
            if (first) {
                ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
                msg.addReceiver(new AID("RestaurantGatekeeperAgent", AID.ISLOCALNAME));
                msg.setLanguage("English");
                msg.setOntology("Reservation-Restaurant-Ontology");
                msg.setContent("Can I book a 2 people table to have dinner on Saturday at 20:00h?");
                send(msg);
                first=false;
            }*/
        }

        @Override
        public boolean done() {
            return ((step == 2 && bestrestaurant == null) || step == 4);
        }
    }
}
