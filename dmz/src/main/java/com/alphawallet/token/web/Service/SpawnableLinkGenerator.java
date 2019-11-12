package com.alphawallet.token.web.Service;

import com.alphawallet.token.entity.SalesOrderMalformed;
import com.alphawallet.token.tools.Numeric;
import com.alphawallet.token.tools.ParseMagicLink;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SpawnableLinkGenerator {

    private static List<BigInteger> tokens = new ArrayList<>();
    private static final String contractAddress = "0x86a2390e15c287d4cb22768d4d1069ef82b7c27b";
    private static ParseMagicLink parseMagicLink = new ParseMagicLink(new CryptoFunctions(), null);
    private static final BigInteger privateKey = BigInteger.TEN;
    private static final int chainId = 3;

    // Time todo put in right format & set each time
    private static final long timestamp = System.currentTimeMillis(); //"20180706210000+0300";
    // Cities
    private static final long COPENHAGEN = 1;
    private static final long SAINT_PETERSBERG = 2;
    private static final long AMSTERDAM = 3;
    private static final long BUCHAREST = 4;
    private static final long LONDON = 5;
    private static final long GLASGLOW = 6;
    private static final long BILBAO = 7;
    private static final long DUBLIN = 8;
    private static final long BUDAPEST = 9;
    private static final long MUNICH = 10;
    private static final long BAKU = 11;
    private static final long ROME = 12;

    // Venues
    private static final long OLIMPICO_IN_ROMA = 1;
    private static final long BAKU_OLYMPIC_STATIUM = 2;
    private static final long PARKEN_STADIUM = 3;
    private static final long SAINT_PETERSBERG_STATIUM = 4;
    private static final long JOHAN_CRUIJFF_STADIUM = 5;
    private static final long NATIONAL_ARENA_BUCHAREST = 6;
    private static final long WEMBLEY_STADIUM = 7;
    private static final long HAMPDEN_PARK = 8;
    private static final long SAN_MAMES_STADIUM = 9;
    private static final long DUBLIN_ARENA = 10;
    private static final long PUSKAS_FERENC_STADIUM = 11;
    private static final long ALLIANZ_ARENA = 12;

    // Teams TODO set each time
    private static final String TEAM_A = "USA";
    private static final String TEAM_B = "AU";

    // Match number TODO set each time
    private static final long MATCH_NUMBER = 1;

    // Numero or sequence number TODO set each time
    private static final long numero = 1;

    // Categories
    private static final long THE_CLUB = 1;
    private static final long THE_LOUNGE = 2;
    private static final long THE_SUITE_SILVER = 3;
    private static final long THE_SUITE_GOLD = 4;

    // Ticket expiry
    private static long expiry = (System.currentTimeMillis() + 1000000000) / 1000L;;


    public static void main(String[] args) throws SalesOrderMalformed {
        //set token ids here
        new SpawnableLinkGenerator(timestamp, LONDON, PARKEN_STADIUM, TEAM_A, TEAM_B, 10);
    }

    private SpawnableLinkGenerator(
            long date,
            long city,
            long venue,
            String teamA,
            String teamB,
            int quantity
    ) throws SalesOrderMalformed {
        // Set values here
        setTokenIds(date, city, venue, teamA, teamB, THE_CLUB, quantity);
        createSpawnableLink();
    }

    private void createSpawnableLink() throws SalesOrderMalformed {
        byte[] message = parseMagicLink.getSpawnableBytes(tokens, contractAddress, BigInteger.ZERO, expiry);
        byte[] signature = signMagicLink(message);
        byte[] linkData = ParseMagicLink.generateSpawnableLeadingLinkBytes(tokens, contractAddress, BigInteger.ZERO, expiry);
        String link = parseMagicLink.completeUniversalLink(chainId, linkData, signature);
        System.out.println(link);
    }

    private void setTokenIds(long date, long city, long venue, String teamA, String teamB, long category, int quantity)
    {
        String tokenId = "";
        tokenId += Numeric.toHexStringNoPrefixZeroPadded(BigInteger.valueOf(date), 38);
        tokenId += Numeric.toHexStringNoPrefixZeroPadded(BigInteger.valueOf(city), 2);
        tokenId += Numeric.toHexStringNoPrefixZeroPadded(BigInteger.valueOf(venue), 2);
        tokenId += Numeric.toHexStringNoPrefixZeroPadded(Numeric.toBigInt(teamA.getBytes()), 6);
        tokenId += Numeric.toHexStringNoPrefixZeroPadded(Numeric.toBigInt(teamB.getBytes()), 6);
        tokenId += Numeric.toHexStringNoPrefixZeroPadded(BigInteger.valueOf(MATCH_NUMBER), 2);
        tokenId += Numeric.toHexStringNoPrefixZeroPadded(BigInteger.valueOf(category), 2);
        tokenId += Numeric.toHexStringNoPrefixZeroPadded(BigInteger.valueOf(numero), 4);
        //pad the final zeros on
        tokenId += Numeric.toHexStringNoPrefixZeroPadded(BigInteger.ZERO, 4);
        BigInteger token = new BigInteger(tokenId, 16);
        while(quantity > 0) {
            tokens.add(token);
            quantity--;
        }
    }

    private byte[] signMagicLink(byte[] signData) {
        ECKeyPair ecKeyPair  = ECKeyPair.create(privateKey);
        Sign.SignatureData signatureData = Sign.signMessage(signData, ecKeyPair);
        return bytesFromSignature(signatureData);
    }

    //TODO this function should be in the libs module not here or in the app
    private static byte[] bytesFromSignature(Sign.SignatureData signature)
    {
        byte[] sigBytes = new byte[65];
        Arrays.fill(sigBytes, (byte) 0);
        try
        {
            System.arraycopy(signature.getR(), 0, sigBytes, 0, 32);
            System.arraycopy(signature.getS(), 0, sigBytes, 32, 32);
            sigBytes[64] = signature.getV();
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }

        return sigBytes;
    }

}

