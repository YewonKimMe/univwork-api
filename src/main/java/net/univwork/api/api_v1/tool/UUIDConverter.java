package net.univwork.api.api_v1.tool;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

@Component
public class UUIDConverter {

     public static byte[] convertUuidToBinary16() {
        UUID uuid = UUID.randomUUID();

        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        return byteBuffer.array();
    }

    public static byte[] convertUuidStringToBinary16(String uuidString) {
        if (uuidString.length() != 36) {
            throw new IllegalArgumentException("Invalid uuid string");
        }
        UUID uuid = UUID.fromString(uuidString);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        return byteBuffer.array();
    }

    public static UUID convertBinary16ToUUID(byte[] binaryData) {
        if (binaryData.length != 16) {
            throw new IllegalArgumentException("Invalid binary data length");
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(binaryData);
        long mostSignificantBits = byteBuffer.getLong();
        long leastSignificantBits = byteBuffer.getLong();

        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
