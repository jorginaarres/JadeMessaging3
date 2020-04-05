
public class Main {
    public static void main(String[] args){
        String[] args1={"-gui","AgentClient:AgentClient"};
        String[] args2={"-container","RestaurantGatekeeperAgent:RestaurantGatekeeperAgent"};
        String[] args3={"-container","RestaurantManagerAgent:RestaurantManagerAgent"};
        jade.Boot.main(args1);
        jade.Boot.main(args2);
        jade.Boot.main(args3);
    }
}
