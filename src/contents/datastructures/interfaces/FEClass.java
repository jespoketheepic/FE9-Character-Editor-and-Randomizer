package contents.datastructures.interfaces;

// The "Pointer"s are pointers to their location in system.cmp
public interface FEClass extends Named, SystemCmpContents{
    String getDisplayName();
    String getJID_name();
    byte[] getJID_pointer();
    String getAID1_name();
    byte[] getAID1_pointer();
    String getAID2_name();
    byte[] getAID2_pointer();
    String getIID_name();
    byte[] getIID_pointer();
    boolean isLaguz();
    boolean isPromoted(); // Laguz response to this is undefined!!
    int getFrequency();
}
