# JadeMessaging
Agent Systems and Applications Homework, delivery 3
This task is a "continuation" of the scenario that you programmed in Homework 2. So, we have a Customer, represented by the ClientAgent agent, who wants to order a table in a restaurant. This time, in the scenario, there are many restaurants that offer their services (they announce them in the Directory Facilitator; agent DF). It would be interesting if the restaurants advertised themselves as serving different types of cuisine (e.g. French, Polish, Spanish, Japanese). In the first step, ClientAgent asks the DF agent for a list of restaurants that serve dishes of the selected cuisine. If the restaurant with a selected cuisine is not available, they receive a negative answer. If such restaurants are available, then it receives (from DF) a list of restaurant GatewayAgent agents and sends to them CallForProposals for "Table booking, 2 people, date, time". Each GatewayAgent corresponds with accept, reject, not-understood randomly (or according to a rule invented by you). GatewayAgent can also ask ManagerAgent about the availability of a table (reusing the code from Homework 2). ClientAgent chooses the restaurant (randomly, or according to the rule invented by you) and the final stages of implementation of the "Call for Proposals" protocol follow - in accordance with its definition as the FIPA standard. 


Using:
Agent Systems and Applications Homework, delivery 2

There are three Agents: Client, RestaurantGatekeeper and RestaurantManager.
Client asks (request) RestaurantGatekeeper if it is possible to book a table for dinner for 2 person for specific day and time. RestaurantGatekeeper asks RestaurantManager
if it is possible. RestaurantManager "checks" availability (generates random answer) and send information to RestaurantGatekeeper.
RestaurantGatekeeper give the answer to Client. In case if the resevation is possible, Client confirms it (by sending appropriate message to 
RestaurantGatekeepe).
Code this scenario, using appropriate communication acts (see presentation JADE:messaging)