import java.io.*; 
import java.util.*; 
import java.net.*; 
  
// Server class 
public class Serve
{ 
    // Vector to store active clients
    static int i=0;
    static Vector<Room> rooms= new Vector<>();	
    public static void main(String[] args) throws IOException, InterruptedException
    {  
        ServerSocket ss = new ServerSocket(1234);   
        Socket s;
        while (true)  
        { 
            //Accept the incoming request 
            s = ss.accept(); 
	    DataOutputStream dos = new DataOutputStream(s.getOutputStream());		
            System.out.println("New client request received : " + s);              
            // obtain input and output streams 
            DataInputStream dis = new DataInputStream(s.getInputStream());                
            dos.writeUTF("Enter Your Name");
	    String name=dis.readUTF();
	    dos.writeUTF("Join room or create room?J/C");
	    char choice=dis.readUTF().charAt(0); 
	    if(choice=='J')
	    {
		dos.writeUTF("Enter room id");
		String inputid=dis.readUTF();
		for(int x=0;x<rooms.size();x++)
		{
		    if(inputid.equals(rooms.get(x).roomid))
		    {
			if(!rooms.get(x).flag)
		   	{
		    	    dos.writeUTF("Group closed");	
		    	    s.close();
		    	    break;
			}
			ClientHandler mtch = new ClientHandler(s,name, dis, dos,rooms.get(x));
			rooms.get(x).addMember(mtch);
			// Create a new Thread with this object. 
                        Thread t = new Thread(mtch);	  	             
                        t.start();  
			break;
		    }		
		}
            }
	    else
	    {
		dos.writeUTF("Your room id is:\n"+i);
		Room ob=new Room();
		ob.roomid=""+i;
		ClientHandler mtch = new ClientHandler(s,name, dis, dos,ob);
		ob.addMember(mtch);
		rooms.add(ob);
		// Create a new Thread with this object. 
                Thread t = new Thread(mtch);	  	             
                t.start();  
		i++;
	    }							               
         }
     }
}               
//Room assign class
class Room
{
    String roomid;	
    boolean flag=true;	
    Vector<ClientHandler> ar = new Vector<>();   
    // counter for clients 
    int i = 0; 
    int []mafias;	
    int []healer;
    int []detective;
    public void addMember(ClientHandler mtch)
    {
	ar.add(mtch);
	i++;
    }
    public void Game() throws IOException, InterruptedException
    {
	try
	{
	mafias=Assign.assignMafia(ar);
	for(int x=0; x<mafias.length; x++)
	{
		ar.get(mafias[x]).dos.writeUTF("You are MAFIA. Other Mafias:\n");
		for(int y=0; y<mafias.length; y++)	
		{					
			if(mafias[y]!=mafias[x])
			ar.get(mafias[x]).dos.writeUTF(ar.get(mafias[y]).name);
		}
		ar.get(mafias[x]).status=1;
	}
	healer=Assign.assignHealer(ar);
	for(int x=0; x<healer.length; x++)
	{
		ar.get(healer[x]).dos.writeUTF("You are Healer. Other Healers:\n");
		for(int y=0; y<healer.length; y++)	
		{					
			if(healer[y]!=healer[x])
			ar.get(healer[x]).dos.writeUTF(ar.get(healer[y]).name);
		}
		ar.get(healer[x]).status=2;				
	}
	/*for(int x=0; x<ar.size(); x++)
	{
	    System.out.println(ar.get(x).status);	
	}*/
	detective=Assign.assignDetective(ar);
	for(int x=0; x<detective.length; x++)
	{
		ar.get(detective[x]).dos.writeUTF("You are Detective. Other Detectives:\n");
		for(int y=0; y<healer.length; y++)	
		{					
			if(detective[y]!=detective[x])
			ar.get(detective[x]).dos.writeUTF(ar.get(detective[y]).name);
		}
		ar.get(detective[x]).status=3;				
	}
	Thread.sleep(5000);
	System.out.println("NOW VOTE");
	}
	catch(Exception e)
	{
	    System.out.println("Error");
	}
    }		
} 
class Assign
{
    static int[] assignMafia(Vector<ClientHandler> ar)//Function for randomly assigning mafia
    {
        Random rand = new Random();
        int n=ar.size(); 
	int tm=0;
	Vector<Integer> numassign=new Vector<>();
        if(n<=5)tm=1;
        else if(n>5 && n<=8)tm=2;
        else if(n<=10)tm=4;
        else if(n<=12)tm=5;
        else if(n<=14)tm=6;
        else tm=8;
        for (int x = 0; x < n; x++) 
        {
             numassign.add(x);
        } 
 	int mafias[]=new int[tm];
	
	for (int x = 0; x < tm; x++) 
        { 
            // take a raundom index between 0 to size 
            // of given List 
            int randomIndex = rand.nextInt(n);	
	    mafias[x]=numassign.get(randomIndex);	
            numassign.remove(randomIndex);		 		
        }
	return mafias;
	
    }
    static int[] assignHealer(Vector<ClientHandler> ar)//Function for randomly assigning healer
    {
        Random rand = new Random();
        int n=ar.size(); 
	int looprun=n;
	int tm=0;
	Vector<Integer> numassign=new Vector<>();
        if(n<=14)tm=1;
        else tm=2;
        for (int x = 0; x < looprun; x++) 
        {
	     if(ar.get(x).status==0)	 
             numassign.add(x);
	     else n--;
        } 
 	int healer[]=new int[tm];
	
	for (int x = 0; x < tm; x++) 
        { 
            int randomIndex = rand.nextInt(n);	
	    healer[x]=numassign.get(randomIndex);	
            numassign.remove(randomIndex);		 		
        }
	return healer;
    }		
    static int[] assignDetective(Vector<ClientHandler> ar)//Function for randomly assigning healer
    {
        Random rand = new Random();
        int n=ar.size(); 
	int looprun=n;
	int tm=0;
	Vector<Integer> numassign=new Vector<>();
        if(n<=14)tm=1;
        else tm=2;
        for (int x = 0; x < looprun; x++) 
        {
	     if(ar.get(x).status==0)	 
             numassign.add(x);
	     else n--;
        } 
 	int detective[]=new int[tm];
	
	for (int x = 0; x < tm; x++) 
        { 
            int randomIndex = rand.nextInt(n);	
	    detective[x]=numassign.get(randomIndex);	
            numassign.remove(randomIndex);		 		
        }
	return detective; 
    }			
}  
// ClientHandler class 
class ClientHandler implements Runnable  
{ 
    Scanner scn = new Scanner(System.in); 
    String name; 
    String roomid;		
    DataInputStream dis; 
    DataOutputStream dos; 
    Socket s; 
    Room id;	
    boolean isloggedin; 
    int status;	     
    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos, Room ob) 
    { 
        this.dis = dis; 
        this.dos = dos; 
        this.name = name; 
        this.s = s;
	id=ob;
        isloggedin=true; 
	status=0;
    } 
  
    @Override
    public void run()
    {
        String received;
        while (true)  
        { 
            try
            { 
                // receive the string 
                received = dis.readUTF(); 
                //System.out.println(received); 
                if(received.equals("logout")){ 
                    this.isloggedin=false; 
                    this.s.close(); 	
                    break; 
                } 
                if(received.equals("Start")&&id.flag==true) 
		{			
			id.flag=false;
			id.Game();
			continue;
                } 
                // break the string into message and recipient part 
                StringTokenizer st = new StringTokenizer(received, ":"); 
                String MsgToSend = st.nextToken(); 
		while(st.hasMoreTokens())
		{
                	String recipient = st.nextToken(); 
                	// search for the recipient in the connected devices list. 
                	// ar is the vector storing client of active users 
                	for (int x=0; x<id.ar.size(); x++)  
                	{
                    		// if the recipient is found, write on its 
                    		// output stream
				ClientHandler mc = id.ar.get(x);
                    		if ((mc.name.equals(recipient)||recipient.equals("all")) && mc.isloggedin==true)  
                    		{ 
					if(!mc.name.equals(this.name))
                        		mc.dos.writeUTF(this.name+" : "+MsgToSend);
                    		}
			} 
                } 
            } catch (Exception e) { 
                  
                e.printStackTrace(); 
            } 
              
        } 
        try 
        { 
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
	} 
    } 
} 
