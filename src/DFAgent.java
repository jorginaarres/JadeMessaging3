import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class DFAgent extends Agent {
    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("reserving");
        sd.setName("restaurant-reserving");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

    }

    protected void takeDown() {
// Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
// Close the GUI
        //myGui.dispose();
// Printout a dismissal message System.out.println(“Seller-agent “+getAID().getName()+” terminating.”); }

    }
}
