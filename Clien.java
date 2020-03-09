import java.io.*; 
import java.util.*; 
import java.net.*; 
  
// Server class 
public class Serve
{ 
  
    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();   
    // counter for clients 
    static int i = 0; 
    static int []mafias;	
    static int []healer;
    static int []detective;
    public static void main(String[] args) throws IOException  
    {  
        ServerSocket ss = new ServerSocket(1234);   
        Socket s;      
        while (true)  
        { 
            //Accept the incoming request 
            s = ss.accept(); 
            if(!ClientHandler.flag)
		{
		    s.close();
		    break;
		}
            System.out.println("New client request received : " + s);              
            // obtain input and output streams 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());               
            dos.writeUTF("Enter Your Name");
	    String name=dis.readUTF();	             
            ClientHandler mtch = new ClientHandler(s,name, dis, dos);   
            // Create a new Thread with this object. 
            Thread t = new Thread(mtch);	  	 
            ar.add(mtch);            
            t.start();  
            i++;
        }

	assignMafia(ar);
	System.out.println(mafias.length);
	for(int x=0; x<mafias.length; x++)
	{
		ar.get(mafias[x]).dos.writeUTF("You are MAFIA. Other Mafias\n");
		for(int y=0; y<mafias.length; y++)	
		{					
			if(y!=mafias[x])
			ar.get(mafias[x]).dos.writeUTF(ar.get(y).name);
		}
	}
	for(int x=0; x<healer.length; x++)
	{
		ar.get(healer[x]).dos.writeUTF("You are Healer. Other Mafias\n");
		for(int y=0; y<healer.length; y++)	
	s	{					
			if(y!=healer[x])
			ar.get(healer[x]).dos.writeUTF(ar.get(y).name);
		}
	}
	/*for(int x=0; x<.length; x++)
	{
		ar.get(mafias[x]).dos.writeUTF("You are MAFIA. Other Mafias\n");
		for(int y=0; y<mafias.length; y++)	
		{					
			if(y!=mafias[x])
			ar.get(mafias[x]).dos.writeUTF(ar.get(y).name);
		}
	}*/
    }  
    static void assignMafia(Vector<ClientHandler> ar)//Function for randomly assigning mafia
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
 	mafias=new int[tm];
	
	for (int x = 0; x < tm; x++) 
        { 
            // take a raundom index between 0 to size 
            // of given List 
            int randomIndex = rand.nextInt(n-1);	
	    mafias[x]=numassign.get(randomIndex);	
            //mafias.add(temporary.get(randomIndex));
            numassign.remove(randomIndex);		 		
        }
    }
    static void assignHealer(Vector<ClientHandler> ar)//Function for randomly assigning mafia
    {
        Random rand = new Random();
        int n=ar.size(); 
	int tm=0;
	Vector<Integer> numassign=new Vector<>();
        if(n<=14)tm=1;
        else tm=2;
        for (int x = 0; x < n; x++) 
        {
             numassign.add(x);
        } 
 	mafias=new int[tm];
	
	for (int x = 0; x < tm; x++) 
        { 
            // take a raundom index between 0 to size 
            // of given List 
            int randomIndex = rand.nextInt(n-1);	
	    healer[x]=numassign.get(randomIndex);	
            //mafias.add(temporary.get(randomIndex));
            numassign.remove(randomIndex);		 		
        }
    }
    static void assign(Vector<ClientHandler> ar)//Function for randomly assigning mafia
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
 	mafias=new int[tm];
	
	for (int x = 0; x < tm; x++) 
        { 
            // take a raundom index between 0 to size 
            // of given List 
            int randomIndex = rand.nextInt(n-1);	
	    mafias[x]=numassign.get(randomIndex);	
            //mafias.add(temporary.get(randomIndex));
            numassign.remove(randomIndex);		 		
        }
    }		
	
}  
// ClientHandler class 
class ClientHandler implements Runnable  
{ 
    Scanner scn = new Scanner(System.in); 
    String name; 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    Socket s; 
    boolean isloggedin; 
    static boolean flag=true;   
    // constructor 
    public ClientHandler(Socket s, String name, 
                            DataInputStream dis, DataOutputStream dos) { 
        this.dis = dis; 
        this.dos = dos; 
        this.name = name; 
        this.s = s; 
        this.isloggedin=true; 
    } 
  
    @Override
    public void run() { 
  
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
		    this.dis.close(); 
                    this.dos.close(); 	
                    break; 
                } 
                if(received.equals("Start")) 
		{			
			flag=false;
			continue;
                } 
                // break the string into message and recipient part 
                StringTokenizer st = new StringTokenizer(received, "#"); 
                String MsgToSend = st.nextToken(); 
		while(st.hasMoreTokens())
		{
                	String recipient = st.nextToken(); 
                        
                	// search for the recipient in the connected devices list. 
                	// ar is the vector storing client of active users 
                	for (int x=0; x<Serve.ar.size(); x++)  
                	{
                    		// if the recipient is found, write on its 
                    		// output stream
				ClientHandler mc = Serve.ar.get(x);
                    		if ((mc.name.equals(recipient)||recipient.equals("all")) && mc.isloggedin==true)  
                    		{ 
                        		mc.dos.writeUTF(this.name+" : "+MsgToSend);
                    		}
			} 
                } 
            } catch (IOException e) { 
                  
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
