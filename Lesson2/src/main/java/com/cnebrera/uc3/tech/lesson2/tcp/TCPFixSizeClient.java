package com.cnebrera.uc3.tech.lesson2.tcp;

import com.cnebrera.uc3.tech.lesson2.util.FixSizeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * TCP client that read messages of fixed size from a server
 */
public class TCPFixSizeClient
{
    public static void main(String argv[]) throws Exception
    {
        // TODO 1 Create the client socket
        final Socket clientSocket=new Socket("localhost", 6789);

        // TODO 2 Get the input stream
        final InputStream inputStream = clientSocket.getInputStream();

        // Read messages non stop
        while(true)
        {
            readMessage(inputStream);
            // TODO 3 call read message with the input stream
        }
    }

    /**
     * Read a message from the given input Stream
     * @param inputStream the input stream
     * @throws IOException exception thrown if there is a problem
     */
    private static void readMessage(final InputStream inputStream) throws IOException
    {
        // The buffer to read the message bytes
        final byte [] msgBytes = new byte[FixSizeMessage.MSG_BINARY_SIZE];

        // TODO 4 We need to have enough bytes in the stream to contain the whole message
        while(inputStream.available() < msgBytes.length);
        // TODO 5 Read the message bytes
        inputStream.read(msgBytes);
        // Create the message
        final FixSizeMessage msg = FixSizeMessage.readMsgFromBinary(ByteBuffer.wrap(msgBytes));

        System.out.println("Msg received " + msg);
    }
}
