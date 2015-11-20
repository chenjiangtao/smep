package com.huawei.insa2.util;

/**
 * �ֽڡ�����������������������֮��Ĳ�ֺ�ת����
 * @author ���ΰ��
 * @version 1.0
 */
public class TypeConvert {

  /**
   * �ֽ�����ת�������͡�(�����ֽ��򣬸��ֽ���ǰ)
   * @param b �ֽ����顣
   * @param offset ��ת���ֽڿ�ʼ��λ�á�
   * @return ������ʽ��
   */
  public static int byte2int(byte[] b,int offset) {
    return  (b[offset+3]&0xff)      | ((b[offset+2]&0xff)<<8) |
           ((b[offset+1]&0xff)<<16) | ((b[offset]&0xff)<<24);
  }

  /**
   * �ֽ�����ת�������͡�(�����ֽ��򣬸��ֽ���ǰ)
   * @param b �ֽ����顣
   * @return ������ʽ��
   */
  public static int byte2int(byte[] b) {
    return  (b[3]&0xff)      | ((b[2]&0xff)<<8) |
           ((b[1]&0xff)<<16) | ((b[0]&0xff)<<24);
  }

  /**
   * �ֽ�����ת���ɳ����͡�(�����ֽ��򣬸��ֽ���ǰ)
   * @param b �ֽ����顣
   * @return ��������ʽ��
   */
  public static long byte2long(byte[] b) {
    return  ((long)b[7]&0xff)      | (((long)b[6]&0xff)<<8)  |
           (((long)b[5]&0xff)<<16) | (((long)b[4]&0xff)<<24) |
           (((long)b[3]&0xff)<<32) | (((long)b[2]&0xff)<<40) |
           (((long)b[1]&0xff)<<48) | ((long)b[0]<<56);
  }

  /**
   * �ֽ�����ת���ɳ����͡�(�����ֽ��򣬸��ֽ���ǰ)
   * @param b �ֽ����顣
   * @return ��������ʽ��
   */
  public static long byte2long(byte[] b,int offset) {
    return  ((long)b[offset+7]&0xff)      | (((long)b[offset+6]&0xff)<<8)  |
           (((long)b[offset+5]&0xff)<<16) | (((long)b[offset+4]&0xff)<<24) |
           (((long)b[offset+3]&0xff)<<32) | (((long)b[offset+2]&0xff)<<40) |
           (((long)b[offset+1]&0xff)<<48) | ((long)b[offset]<<56);
  }

  /**
   * ����ת�����ֽڡ�(�����ֽ��򣬸��ֽ���ǰ)
   * @param n ������
   * @return ����Ϊ4���ֽ����顣
   */
  public static byte[] int2byte(int n) {
    byte[] b = new byte[4];
    b[0]=(byte)(n>>24);
    b[1]=(byte)(n>>16);
    b[2]=(byte)(n>>8);
    b[3]=(byte)n;
    return b;
  }

  /**
   * ����ת�����ֽڡ�(�����ֽ��򣬸��ֽ���ǰ)
   * @param n ������
   * @param buf ���ת��������ֽ����顣
   * @param offset ���λ�õ�ƫ�Ƶ�ַ��
   */
  public static void int2byte(int n,byte[] buf,int offset) {
    buf[offset]=(byte)(n>>24);
    buf[offset+1]=(byte)(n>>16);
    buf[offset+2]=(byte)(n>>8);
    buf[offset+3]=(byte)n;
  }
  /**
   * ������ת�����ֽڡ�(�����ֽ��򣬸��ֽ���ǰ)
   * @param n ������
   * @return ����Ϊ4���ֽ����顣
   */
  public static byte[] short2byte(int n) {
    byte[] b = new byte[2];
    b[0]=(byte)(n>>8);
    b[1]=(byte)n;
    return b;
  }

  /**
   * ������ת�����ֽڡ�(�����ֽ��򣬸��ֽ���ǰ)
   * @param n ������
   * @param buf ���ת��������ֽ����顣
   * @param offset ���λ�õ�ƫ�Ƶ�ַ��
   */
  public static void short2byte(int n,byte[] buf,int offset) {
    buf[offset]=(byte)(n>>8);
    buf[offset+1]=(byte)n;
  }

  /**
   * ������ת�����ֽڡ�(�����ֽ��򣬸��ֽ���ǰ)
   * @param n ��������
   * @return ����Ϊ8���ֽ����顣
   */
  public static byte[] long2byte(long n) {
    byte[] b = new byte[8];
    //b[0]=(byte)(n>>57); // comment by edong 20011203
    b[0]=(byte)(n>>56);
    b[1]=(byte)(n>>48);
    b[2]=(byte)(n>>40);
    b[3]=(byte)(n>>32);
    b[4]=(byte)(n>>24);
    b[5]=(byte)(n>>16);
    b[6]=(byte)(n>>8);
    b[7]=(byte)n;
    return b;
  }

  /**
   * ������ת�����ֽڡ�(�����ֽ��򣬸��ֽ���ǰ)
   * @param n ��������
   * @param buf ���ת��������ֽ����顣
   * @param offset ���λ�õ�ƫ�Ƶ�ַ��
   */
  public static void long2byte(long n,byte[] buf,int offset) {
    //buf[offset]=(byte)(n>>57); // comment by edong 20011203
    buf[offset]=(byte)(n>>56);
    buf[offset+1]=(byte)(n>>48);
    buf[offset+2]=(byte)(n>>40);
    buf[offset+3]=(byte)(n>>32);
    buf[offset+4]=(byte)(n>>24);
    buf[offset+5]=(byte)(n>>16);
    buf[offset+6]=(byte)(n>>8);
    buf[offset+7]=(byte)n;
  }

}
