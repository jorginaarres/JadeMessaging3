import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DFGui extends JFrame {
    private DFAgent myAgent;
    private JTextField restaurantName, restaurantType;
    DFGui(DFAgent a) {
        super(a.getLocalName());

        myAgent = a;

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.add(new JLabel("Restaurant name:"));
        restaurantName = new JTextField(15);
        p.add(restaurantName);
        p.add(new JLabel("Restaurant type:"));
        restaurantType = new JTextField(15);
        p.add(restaurantType);
        getContentPane().add(p, BorderLayout.CENTER);

        JButton addButton = new JButton("Add");
        addButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    String name = restaurantName.getText().trim();
                    String type = restaurantType.getText().trim();
                    myAgent.updateCatalogue(name,type);
                    restaurantName.setText("");
                    restaurantType.setText("");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DFGui.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } );
        p = new JPanel();
        p.add(addButton);
        getContentPane().add(p, BorderLayout.SOUTH);

        // Make the agent terminate when the user closes
        // the GUI using the button on the upper right corner
        addWindowListener(new   WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                myAgent.doDelete();
            }
        } );

        setResizable(false);
    }

    public void show() {
        super.show();
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int)screenSize.getWidth() / 2;
        int centerY = (int)screenSize.getHeight() / 2;
        setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
    }
}
