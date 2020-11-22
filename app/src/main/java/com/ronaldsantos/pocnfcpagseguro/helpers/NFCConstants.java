package com.ronaldsantos.pocnfcpagseguro.helpers;

public class NFCConstants {

//    public static final int VALUE_BLOCK = 1;
//    public static final int ID_CASHIER_BLOCK = 2;
//    public static final int CPF_BLOCK = 8;
//    public static final int TAG_BLOCK = 9;
//    public static final int NAME_BLOCK = 10;
//    public static final int CELL_PHONE_BLOCK = 11;

    /**
     *
     * Each sector has 4 blocks and each block can store up to 16 bytes.
     * The fourth block of each sector is stored the access permissions,
     * care must be taken not to write in this block anything other than the access permissions
     *
     * Remember that in the array first block is 0 and not 1,
     * be aware of this when writing access permissions
     * and even data to avoid writing data to the access permission block
     *
     */

    public static final int NAME_BLOCK = 20;
    public static final int BIRTHDAY_BLOCK = 21;
    public static final int ADDRESS_BLOCK = 22;
    public static final int MOTHER_BLOCK = 24;
    public static final int FATHER_BLOCK = 25;
    public static final int CELL_PHONE_BLOCK = 26;

    // teste

}
