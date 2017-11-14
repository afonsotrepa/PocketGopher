package com.gmail.afonsotrepa.pocketgopher.gopherclient;


import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Connection {
    private Socket socket;
    private PrintWriter os; //output stream
    private BufferedReader is; //input stream

    /**
     * Opens a connection with the server
     *
     * @param server ip or DNS address of the server
     * @param port   port the server and the client listen to
     */
    Connection(String server, Integer port) throws IOException {
        socket = new Socket(server, port);

        os = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        is = new BufferedReader((new InputStreamReader((socket.getInputStream()))));
    }

    /**
     * Sends/writes a string to the server
     *
     * @param message string to send to the server
     */
    private void write(String message) {
        os.println(message);
        os.flush();
    }

    /**
     * Receives/reads from the server
     *
     * @return a message sent by the server
     */
    private String read() {
        StringBuilder sb = new StringBuilder();
        String line;
        try {

            //read until the end of the message (EOF or ".")
            while ((line = is.readLine()) != null && !line.equals(".")) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * Sends the selector to the server and returns the response (excpects a directory/menu)
     *
     * @param selector selector (see RFC 1436)
     * @return the response from the server (as GopherLine objects)
     */
    List<GopherLine> getMenu(String selector) {
        this.write(selector); //send the selector
        String lines[] = this.read().split("\n"); //read the response by the server

        List<GopherLine> response = new ArrayList<>();

        for (String line : lines) {
            //skip line if empty
            if (line.equals(""))
                continue;

            String[] linesplit = line.split("\t");
            switch (line.charAt(0)) {
                case '0': //text file
                    response.add(new TextFileGopherLine(
                            linesplit[0].substring(1), //remove the type tag
                            linesplit[1],
                            linesplit[2],
                            Integer.parseInt(linesplit[3])));
                    break;

                case '1': //menu/directory
                    response.add(
                            new MenuGopherLine(
                                    linesplit[0].substring(1), //remove the type tag
                                    linesplit[1],
                                    linesplit[2],
                                    Integer.parseInt(linesplit[3])));
                    break;

                case '7': //Search engine or CGI script
                    response.add(
                            new SearchGopherLine(
                                    linesplit[0].substring(1), //remove the type tag
                                    linesplit[1],
                                    linesplit[2],
                                    Integer.parseInt(linesplit[3])));
                    break;

                case 'i': //Informational text (not in the protocol but common)
                    if (linesplit.length < 2) {
                        response.add(new TextGopherLine(linesplit[0].substring(1)));
                    }
                    else {
                        response.add(new TextGopherLine(linesplit[0].substring(1), linesplit[2]));
                    }
                    break;

                case 'g': //gif (temporary)
                case 'I': //Image
                    response.add(
                            new ImageGopherLine(
                                    linesplit[0].substring(1), //remove the type tag
                                    linesplit[1],
                                    linesplit[2],
                                    Integer.parseInt(linesplit[3])));
                    break;

                case 'h': //html
                    response.add(
                            new HtmlGopherLine(
                                    linesplit[0].substring(1), //remove the type tag
                                    linesplit[1],
                                    linesplit[2],
                                    Integer.parseInt(linesplit[3])));
                    break;

                case '4': //macintosh binhex file
                case '5': //binary archive
                case '6': //uuencoded file
                case '9': //binary file
                case 'd': //word-processing document
                    response.add(
                            new BinGopherLine(
                                    linesplit[0].substring(1), //remove the type tag
                                    linesplit[1],
                                    linesplit[2],
                                    Integer.parseInt(linesplit[3])));
                    break;

                case ';': //video file
                    response.add(
                            new VideoGopherLine(
                                    linesplit[0].substring(1), //remove the type tag
                                    linesplit[1],
                                    linesplit[2],
                                    Integer.parseInt(linesplit[3])));
                    break;

                case '3':
                default:
                    if (linesplit.length >= 3) {
                        new VideoGopherLine(
                                linesplit[0].substring(1), //remove the type tag
                                linesplit[1],
                                linesplit[2],
                                Integer.parseInt(linesplit[3]));
                    } else {
                        response.add(new UnknownGopherLine(line));
                    }
            }
        }

        return response;
    }

    /**
     * Sends the selector to the server and returns the response (expects text)
     *
     * @param selector selector (see RFC 1436)
     * @return the response from the server (as strings)
     */
    String getText(String selector) {
        this.write(selector); //send the selector
        return this.read();
    }

    Drawable getDrawable(String selector) throws IOException {
        this.write(selector); //send the selector
        return Drawable.createFromStream(socket.getInputStream(), null);

    }

    /**
     * Sends the selector to the server and writes its response to the file
     * @param selector selector (see RFC 1436)
     * @param file file to store the response from the server
     */
    void getBinary(String selector, File file) {
        this.write(selector); //send the selector

        try {
            InputStream is = this.socket.getInputStream();
            FileOutputStream os = new FileOutputStream(file);

            int read = -1;
            byte[] buf = new byte[4096]; //pretty small rn

            while ((read = is.read(buf)) != -1) {
                os.write(buf, 0, read);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        finally {
            os.flush(); //flush the buffer
            os.close(); //close the stream
        }
    }
}
