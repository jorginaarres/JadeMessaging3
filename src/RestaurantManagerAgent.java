import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RestaurantManagerAgent extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
               // MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println( "Restaurant Manager Agent has recieved: " + msg.getContent());
                    ACLMessage response = msg.createReply();
                    response.setPerformative(ACLMessage.INFORM);

                    // generate random response
                    boolean free = Math.random() < 0.5;
                    String isfreeresponse;
                    if (free){
                        isfreeresponse= "is Free";
                    }
                    else isfreeresponse= "is not Free";


                    response.setContent(isfreeresponse);
                    send(response);
                    System.out.println( "Restaurant Manager Agent has answered: " + response.getContent());
                }else block();
            }
        });

    };

}
