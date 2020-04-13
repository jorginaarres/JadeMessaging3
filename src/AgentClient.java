import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class AgentClient extends Agent {
    private String CustomerRestaurantWantedType;
    private AID[] sellerAgents = {
            new AID("RESTAURANT1", AID.ISLOCALNAME),
            new AID("RESTAURANT1", AID.ISLOCALNAME)};

    @Override
    protected void setup() {

        System.out.println("Hallo! Buyer-agent "+ getAID().getName() +" is ready.");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            CustomerRestaurantWantedType = "Spanish";
            System.out.println("Trying to buy " + args[0]);
            addBehaviour(new TickerBehaviour(this, 10000) {

                protected void onTick() {
                    // Update the list of agents
                    System.out.println("Ticker behaviour:");
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("reserving");
                    template.addServices(sd);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        AID[] tempAgents = new AID[result.length];
                        System.out.println("AgentsNames:");
                        for (int i = 0; i < result.length; ++i) {
                            tempAgents[i] = result[i].getName();
                            System.out.println(result[i].getName());

                        }
                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }
                    // Perform the request
                    myAgent.addBehaviour(new RequestPerformer());
                }
            });
        }

    }
    class RequestPerformer extends Behaviour {
        private boolean first = true;

        @Override
        public void action() {
            ACLMessage proposal = receive();
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
            }
        }

        @Override
        public boolean done() {
            return false;
        }
    }
}
