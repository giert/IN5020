package TasteProfile;


/**
* TasteProfile/SongProfileHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from tasteprofile.idl
* 27. september 2019 kl 23.23 CEST
*/

abstract public class SongProfileHelper
{
  private static String  _id = "IDL:TasteProfile/SongProfile:1.0";


  public static void insert (org.omg.CORBA.Any a, TasteProfile.SongProfile that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static TasteProfile.SongProfile extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.ValueMember[] _members0 = new org.omg.CORBA.ValueMember[2];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          // ValueMember instance for total_play_count
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_long);
          _members0[0] = new org.omg.CORBA.ValueMember ("total_play_count", 
              "", 
              _id, 
              "", 
              _tcOf_members0, 
              null, 
              org.omg.CORBA.PUBLIC_MEMBER.value);
          // ValueMember instance for top_three_users
          _tcOf_members0 = TasteProfile.TopThreeUsersHelper.type ();
          _members0[1] = new org.omg.CORBA.ValueMember ("top_three_users", 
              TasteProfile.TopThreeUsersHelper.id (), 
              _id, 
              "", 
              _tcOf_members0, 
              null, 
              org.omg.CORBA.PUBLIC_MEMBER.value);
          __typeCode = org.omg.CORBA.ORB.init ().create_value_tc (_id, "SongProfile", org.omg.CORBA.VM_NONE.value, null, _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static TasteProfile.SongProfile read (org.omg.CORBA.portable.InputStream istream)
  {
    return (TasteProfile.SongProfile)((org.omg.CORBA_2_3.portable.InputStream) istream).read_value (id ());
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, TasteProfile.SongProfile value)
  {
    ((org.omg.CORBA_2_3.portable.OutputStream) ostream).write_value (value, id ());
  }


}
