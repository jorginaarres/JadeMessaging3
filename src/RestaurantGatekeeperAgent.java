import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;

public class RestaurantGatekeeperAgent extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null ) {

                    if(msg.getPerformative() == ACLMessage.QUERY_IF){
                        System.out.println("Restaurant Gatekeeper has recieved: " + msg.getContent());

                        ACLMessage msg2 = new ACLMessage(ACLMessage.QUERY_IF);
                        msg2.addReceiver(new AID("RestaurantManagerAgent", AID.ISLOCALNAME));
                        msg2.setLanguage(msg.getLanguage());
                        msg2.setOntology(msg.getOntology());
                        msg2.setContent(msg.getContent());
                        this.myAgent.send(msg2);
                    }
                    else if(msg.getPerformative() == ACLMessage.CONFIRM) {//ask client if he wants to reserve
                        ACLMessage initiation = new ACLMessage(ACLMessage.PROPOSE);
                        initiation.addReceiver(new AID("AgentClient", AID.ISLOCALNAME));
                        initiation.setContent("We have space for two this day an time, do you want to do the reservation?");
                        addBehaviour(new ProposeInitiator(this.myAgent, initiation){});
                        System.out.println( "Restaurant Gatekeeper Agent has answered the Client: " + initiation.getContent());

                    }
                    else if(msg.getPerformative() == ACLMessage.REFUSE) {
                        // comunicate to client that there is no place.
                        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                        response.addReceiver(new AID("AgentClient", AID.ISLOCALNAME));
                        response.setPerformative(ACLMessage.INFORM);
                        response.setContent("Sorry, we don't have space for two this day an time");
                        send(response);
                        System.out.println("Restaurant Gatekeeper Agent has answered the Client: " + response.getContent());
                    }
                  //  this.myAgent.receive();

                }
                else block();
            }
        });


    };

}
