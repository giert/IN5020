package TasteProfile;


/**
* TasteProfile/UserCounter.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from tasteprofile.idl
* 26. september 2019 kl 13.06 CEST
*/

public abstract class UserCounter implements org.omg.CORBA.portable.StreamableValue
{
  public String user_id = null;
  public int songid_play_time = (int)0;

  private static String[] _truncatable_ids = {
    TasteProfile.UserCounterHelper.id ()
  };

  public String[] _truncatable_ids() {
    return _truncatable_ids;
  }

  public void _read (org.omg.CORBA.portable.InputStream istream)
  {
    this.user_id = istream.read_string ();
    this.songid_play_time = istream.read_long ();
  }

  public void _write (org.omg.CORBA.portable.OutputStream ostream)
  {
    ostream.write_string (this.user_id);
    ostream.write_long (this.songid_play_time);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return TasteProfile.UserCounterHelper.type ();
  }
} // class UserCounter
