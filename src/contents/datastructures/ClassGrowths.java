package contents.datastructures;

public class ClassGrowths {
    private byte[] growths;

    public ClassGrowths(byte[] growths) {
        if(growths.length != 8){
            throw new IllegalArgumentException();
        }

        this.growths = growths.clone();
    }

    public byte[] getGrowths() {
        return growths;
    }

    public void setGrowths(byte[] growths) {
        if(growths.length != 8){
            throw new IllegalArgumentException();
        }

        this.growths = growths.clone();
    }

    public void massEdit_All(int edit){
        for (int i = 0; i < 8; i++) {
            switch (byteRangeCheck(growths[i] + edit)){
                case 1:
                    growths[i] = (byte) 255;
                    break;
                case -1:
                    growths[i] = 0;
                    break;
                case 0:
                    growths[i] += edit;
                    break;
            }
        }
    }

    public void massEdit_StrMagSklSpd(int edit){
        for (int i = 1; i < 5; i++) {
            switch (byteRangeCheck(growths[i] + edit)){
                case 1:
                    growths[i] = (byte) 255;
                    break;
                case -1:
                    growths[i] = 0;
                    break;
                case 0:
                    growths[i] += edit;
                    break;
            }
        }
    }

    public void massEdit_StrMag(int edit){
        for (int i = 1; i < 3; i++) {
            switch (byteRangeCheck(growths[i] + edit)){
                case 1:
                    growths[i] = (byte) 255;
                    break;
                case -1:
                    growths[i] = 0;
                    break;
                case 0:
                    growths[i] += edit;
                    break;
            }
        }
    }

    // Returns 1 if it would overflow a byte cast, -1 if it would underflow, 0 if it can be byte cast safely
    private int byteRangeCheck(int i){
        if (i < 0){
            return -1;
        } else if (i > 255){
            return 1;
        } else {
            return 0;
        }
    }
}
