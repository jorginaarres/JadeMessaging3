import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeResponder;

public class AgentClient extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new SimpleBehaviour() {
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
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
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
        );
    };

}
