package TasteProfile;


/**
* TasteProfile/UserCounterDefaultFactory.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from tasteprofile.idl
* 26. september 2019 kl 13.06 CEST
*/

public class UserCounterDefaultFactory implements org.omg.CORBA.portable.ValueFactory {

  public java.io.Serializable read_value (org.omg.CORBA_2_3.portable.InputStream is)
  {
    return is.read_value(new UserCounterImpl ());
  }
}
