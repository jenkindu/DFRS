package server;


/**
* server/ServerInterfacePOA.java .
* ��IDL-to-Java ������ (����ֲ), �汾 "3.2"����
* ��C:/Users/nathan/workspace/COMP6231project/src/server.idl
* 2016��11��15�� ���ڶ� ����02ʱ42��26�� EST
*/

public abstract class ServerInterfacePOA extends org.omg.PortableServer.Servant
 implements server.ServerInterfaceOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("bookFlight", new java.lang.Integer (0));
    _methods.put ("getBookedFlightCount", new java.lang.Integer (1));
    _methods.put ("editFlightRecord", new java.lang.Integer (2));
    _methods.put ("transferReservation", new java.lang.Integer (3));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // server/ServerInterface/bookFlight
       {
         String firstName = in.read_string ();
         String lastName = in.read_string ();
         String address = in.read_string ();
         String phoneNumber = in.read_string ();
         String destination = in.read_string ();
         String flightClass = in.read_string ();
         String flightDate = in.read_string ();
         String $result = null;
         $result = this.bookFlight (firstName, lastName, address, phoneNumber, destination, flightClass, flightDate);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // server/ServerInterface/getBookedFlightCount
       {
         String managerID = in.read_string ();
         String $result = null;
         $result = this.getBookedFlightCount (managerID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // server/ServerInterface/editFlightRecord
       {
         String managerID = in.read_string ();
         String destination = in.read_string ();
         String flightDate = in.read_string ();
         int economy = in.read_long ();
         int business = in.read_long ();
         int firstclass = in.read_long ();
         String $result = null;
         $result = this.editFlightRecord (managerID, destination, flightDate, economy, business, firstclass);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 3:  // server/ServerInterface/transferReservation
       {
         String managerID = in.read_string ();
         String PassengerID = in.read_string ();
         String CurrentCity = in.read_string ();
         String OtherCity = in.read_string ();
         String $result = null;
         $result = this.transferReservation (managerID, PassengerID, CurrentCity, OtherCity);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:server/ServerInterface:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public ServerInterface _this() 
  {
    return ServerInterfaceHelper.narrow(
    super._this_object());
  }

  public ServerInterface _this(org.omg.CORBA.ORB orb) 
  {
    return ServerInterfaceHelper.narrow(
    super._this_object(orb));
  }


} // class ServerInterfacePOA