package TasteProfile;

/**
* TasteProfile/TopThreeUsersHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from tasteprofile.idl
* 26. september 2019 kl 13.06 CEST
*/

public final class TopThreeUsersHolder implements org.omg.CORBA.portable.Streamable
{
  public TasteProfile.TopThreeUsers value = null;

  public TopThreeUsersHolder ()
  {
  }

  public TopThreeUsersHolder (TasteProfile.TopThreeUsers initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = TasteProfile.TopThreeUsersHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    TasteProfile.TopThreeUsersHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return TasteProfile.TopThreeUsersHelper.type ();
  }

}