package TasteProfile;

/**
* TasteProfile/SongCounterHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from tasteprofile.idl
* 3. oktober 2019 kl 15.37 CEST
*/

public final class SongCounterHolder implements org.omg.CORBA.portable.Streamable
{
  public TasteProfile.SongCounter value = null;

  public SongCounterHolder ()
  {
  }

  public SongCounterHolder (TasteProfile.SongCounter initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = TasteProfile.SongCounterHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    TasteProfile.SongCounterHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return TasteProfile.SongCounterHelper.type ();
  }

}
