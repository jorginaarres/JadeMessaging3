import jade.content.lang.sl.SLVocabulary;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static jade.content.lang.sl.SLVocabulary.*;

public class RestaurantManagerAgent extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
                ACLMessage msg = receive(mt);
                if (msg != null) {
                    System.out.println( "Restaurant Manager Agent has recieved: " + msg.getContent());
                    // generate random response
                    boolean free = Math.random() < 0.5;
                    String isfreeresponse;
                    if (free){
                        isfreeresponse= "is Free";
                    }
                    else isfreeresponse= "is not Free";

                    ACLMessage reply = msg.createReply();
                    if(free){reply.setPerformative(ACLMessage.CONFIRM);}
                    else reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent(isfreeresponse);
                    send(reply);

                    System.out.println( "Restaurant Manager Agent has answered: " + reply.getContent());
                }else block();
            }
        });

    };

}
