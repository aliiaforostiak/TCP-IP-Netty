package tcp;

public class Protocol {

    public static byte[] encodeLength(String message) {

        byte[] data = message.getBytes();
        int length = data.length;

        byte[] result = new byte[4 + length];
        result[0] = (byte) (length >> 24);
        result[1] = (byte) (length >> 16);
        result[2] = (byte) (length >> 8);
        result[3] = (byte) (length);

        System.arraycopy(data, 0, result, 4, length);
        return result;

    }

    public static int decodeLength(byte[] lengthBytes) {
        return ((lengthBytes[0] & 0xFF) << 24)
                | ((lengthBytes[1] & 0xFF) << 16)
                | ((lengthBytes[2] & 0xFF) << 8)
                | (lengthBytes[3] & 0xFF);
    }
}
