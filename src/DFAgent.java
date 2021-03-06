import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class DFAgent extends Agent {
    private Hashtable<String, String> catalogue;
    private DFGui myGui;

    @Override
    protected void setup()
    {
        catalogue= new Hashtable<String, String>();
        myGui= new DFGui(this);
        myGui.show();


        // Register the resgaurant-selling service in the directory facilitator
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("restaurant-to-reserve");
        sd.setName("JADE-restaurant-booking");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add the behaviour serving requests for
        //offer from buyer agents
        addBehaviour(new OfferRequestsServer());
        // Add the behaviour serving purchase orders from
        //buyer agents
        addBehaviour(new PurchaseOrdersServer());

    }

    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Close the GUI
        myGui.dispose();
        // Printout a dismissal message
        System.out.println("DF-agent "+getAID().getName()+"terminating.");

    }
    public void updateCatalogue(final String name, final String type) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                catalogue.put(name, type);
                System.out.println("You have added a restaurant in the catalogue, catalogue:");
                System.out.println(catalogue);

            } }
        );
    }

    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                System.out.println("Offer requests server message received");

                // Message received. Process it
                String type = msg.getContent();
                ACLMessage reply = msg.createReply();

                String name = catalogue.get(type);



                if (catalogue.containsValue(type)) {
                // The requested restaurant is available to reserve.
                // Reply with the type

                    Set<String> keys = catalogue.keySet();
                    Iterator<String> itr= keys.iterator();
                    String str;
                    Hashtable<String,String> catalogeType = new Hashtable<>();
                    while(itr.hasNext()){
                        str = itr.next();
                        if(catalogue.get(str).equals(type)){
                            catalogeType.put(str,type);
                        }
                    }

                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(String.valueOf(catalogeType));
                } else {
                    // The requested restaurant is NOT available to reserve.
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent( "not-available" );
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    } // End of inner class OfferRequestsServer

    private class PurchaseOrdersServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println("Purchase orders server message received");

                // ACCEPT_PROPOSAL Message received. Process it
                String name = msg.getContent();
                ACLMessage reply = msg.createReply();

                String type = catalogue.remove(name);
                if (type != null) {
                    reply.setPerformative(ACLMessage.INFORM);
                    System.out.println(name+" sold to agent "+msg.getSender().getName());
                } else {
                    // The requested book has been sold to another buyer in the meanwhile .
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }  // End of inner class OfferRequestsServer
}
