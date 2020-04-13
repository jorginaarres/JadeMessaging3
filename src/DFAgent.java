import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.Hashtable;

public class DFAgent extends Agent {
    private Hashtable<String, String> catalogue;
    private DFGui myGui;

    @Override
    protected void setup()
    {
        catalogue= new Hashtable<String, String>();
        myGui= new DFGui(this);
        myGui.show();

        ServiceDescription sd  = new ServiceDescription();
        sd.setType( "reserving" );
        sd.setName( getLocalName() );
        register( sd );
    }
    void register( ServiceDescription sd)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd );
        }
        catch (FIPAException fe) { fe.printStackTrace(); }
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
            } }
        );
    }
}
