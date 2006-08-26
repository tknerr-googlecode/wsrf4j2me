// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst safe 

package org.kobjects.base64;

import java.io.*;

public class Base64
{

    static final char charTab[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    public Base64()
    {
    }

    public static String encode(byte abyte0[])
    {
        return encode(abyte0, 0, abyte0.length, ((StringBuffer) (null))).toString();
    }

    public static StringBuffer encode(byte abyte0[], int i, int j, StringBuffer stringbuffer)
    {
        if(stringbuffer == null)
        {
            stringbuffer = new StringBuffer((abyte0.length * 3) / 2);
        }
        int k = j - 3;
        int l = i;
        int i1 = 0;
        do
        {
            if(l > k)
            {
                break;
            }
            int j1 = (abyte0[l] & 0xff) << 16 | (abyte0[l + 1] & 0xff) << 8 | abyte0[l + 2] & 0xff;
            stringbuffer.append(charTab[j1 >> 18 & 0x3f]);
            stringbuffer.append(charTab[j1 >> 12 & 0x3f]);
            stringbuffer.append(charTab[j1 >> 6 & 0x3f]);
            stringbuffer.append(charTab[j1 & 0x3f]);
            l += 3;
            if(i1++ >= 14)
            {
                i1 = 0;
                stringbuffer.append("\r\n");
            }
        } while(true);
        if(l == (i + j) - 2)
        {
            int k1 = (abyte0[l] & 0xff) << 16 | (abyte0[l + 1] & 0xff) << 8;
            stringbuffer.append(charTab[k1 >> 18 & 0x3f]);
            stringbuffer.append(charTab[k1 >> 12 & 0x3f]);
            stringbuffer.append(charTab[k1 >> 6 & 0x3f]);
            stringbuffer.append("=");
        } else
        if(l == (i + j) - 1)
        {
            int l1 = (abyte0[l] & 0xff) << 16;
            stringbuffer.append(charTab[l1 >> 18 & 0x3f]);
            stringbuffer.append(charTab[l1 >> 12 & 0x3f]);
            stringbuffer.append("==");
        }
        return stringbuffer;
    }

    static int decode(char c)
    {
        if(c >= 'A' && c <= 'Z')
        {
            return c - 65;
        }
        if(c >= 'a' && c <= 'z')
        {
            return (c - 97) + 26;
        }
        if(c >= '0' && c <= '9')
        {
            return (c - 48) + 26 + 26;
        }
        switch(c)
        {
        case 43: // '+'
            return 62;

        case 47: // '/'
            return 63;

        case 61: // '='
            return 0;
        }
        throw new RuntimeException("unexpected code: " + c);
    }

    public static byte[] decode(String s)
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        try
        {
            decode(s, ((OutputStream) (bytearrayoutputstream)));
        }
        catch(IOException ioexception)
        {
            throw new RuntimeException();
        }
        return bytearrayoutputstream.toByteArray();
    }

    public static void decode(String s, OutputStream outputstream)
        throws IOException
    {
        int i = 0;
        do
        {
            int j;
            for(j = s.length(); i < j && s.charAt(i) <= ' '; i++) { }
            if(i == j)
            {
                break;
            }
            int k = (decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12) + (decode(s.charAt(i + 2)) << 6) + decode(s.charAt(i + 3));
            outputstream.write(k >> 16 & 0xff);
            if(s.charAt(i + 2) == '=')
            {
                break;
            }
            outputstream.write(k >> 8 & 0xff);
            if(s.charAt(i + 3) == '=')
            {
                break;
            }
            outputstream.write(k & 0xff);
            i += 4;
        } while(true);
    }

}
