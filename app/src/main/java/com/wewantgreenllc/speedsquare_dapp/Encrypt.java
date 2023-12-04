package com.wewantgreenllc.speedsquare_dapp;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {

    public static String MD5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }





}





//
//  Encrypt.swift
//  Speed Square
//
//  Created by Steven Grutman on 9/14/23.
//
//
//import Foundation
//        import CryptoKit
//        import CommonCrypto
//
//        struct AES {
//
//private let key: Data
//private let iv: Data
//
//        init?(key: String, iv: String) {
//        guard key.count == kCCKeySizeAES128 || key.count == kCCKeySizeAES256, let keyData = key.data(using: .utf8) else {
//        debugPrint("Error: Failed to set a key.")
//        return nil
//        }
//        guard iv.count == kCCBlockSizeAES128, let ivData = iv.data(using: .utf8) else {
//        debugPrint("Error: Failed to set an initial vector.")
//        return nil
//        }
//        self.key = keyData
//        self.iv  = ivData
//        }
//
//
//        func encrypt(string: String) -> Data? {
//        return crypt(data: string.data(using: .utf8), option: CCOperation(kCCEncrypt))
//        }
//
//        func decrypt(data: Data?) -> String? {
//        guard let decryptedData = crypt(data: data, option: CCOperation(kCCDecrypt)) else { return nil }
//        return String(bytes: decryptedData, encoding: .utf8)
//        }
//
//        func crypt(data: Data?, option: CCOperation) -> Data? {
//        guard let data = data else { return nil }
//        let cryptLength = data.count + key.count
//        var cryptData   = Data(count: cryptLength)
//        var bytesLength = Int(0)
//        let status = cryptData.withUnsafeMutableBytes { cryptBytes in
//        data.withUnsafeBytes { dataBytes in
//        iv.withUnsafeBytes { ivBytes in
//        key.withUnsafeBytes { keyBytes in
//        CCCrypt(option, CCAlgorithm(kCCAlgorithmAES), CCOptions(kCCOptionPKCS7Padding), keyBytes.baseAddress, key.count, ivBytes.baseAddress, dataBytes.baseAddress, data.count, cryptBytes.baseAddress, cryptLength, &bytesLength)
//        }
//        }
//        }
//        }
//        guard Int32(status) == Int32(kCCSuccess) else {
//        debugPrint("Error: Failed to crypt data. Status \(status)")
//        return nil
//        }
//        cryptData.removeSubrange(bytesLength..<cryptData.count)
//        return cryptData
//        }
//        }
//
//        func convertDictionaryToJSON(_ dictionary: [String: Any]) -> String? {
//        guard let jsonData = try? JSONSerialization.data(withJSONObject: dictionary, options: .prettyPrinted) else {
//        print("Something is wrong while converting dictionary to JSON data.")
//        return nil
//        }
//        guard let jsonString = String(data: jsonData, encoding: .utf8) else {
//        print("Something is wrong while converting JSON data to JSON string.")
//        return nil
//        }
//        return jsonString
//        }
//
//        func generateRandomString(length: Int) -> String {
//        // each hexadecimal character represents 4 bits, so we need 2 hex characters per byte
//        let byteCount = length / 2
//        var bytes = [UInt8](repeating: 0, count: byteCount)
//        let result = SecRandomCopyBytes(kSecRandomDefault, byteCount, &bytes)
//        guard result == errSecSuccess else {
//        fatalError("Failed to generate random bytes: \(result)")
//        }
//        // convert to hex string
//        let hexString = bytes.map { String(format: "%02x", $0) }.joined()
//        let paddedHexString = hexString.padding(toLength: length, withPad: "0", startingAt: 0)
//        return paddedHexString
//        }
//
//        func MD5(string: String) -> String {
//        let digest = Insecure.MD5.hash(data: Data(string.utf8))
//
//        return digest.map {
//        String(format: "%02hhx", $0)
//        }.joined()
//        }
//
//        func getAppleUsername(str: String) -> String{
//        return "apple_" + MD5(string: str)
//        }
//
//
//        func encryptLogin(username: Optional<String>, password: Optional<String>, email: Optional<String>, signature: Optional<String>, pub_key: Optional<String>, epoch: Int, saved: Bool) -> String {
////    print(saved)
//        let derivedPassword = username != nil ? (saved ? password! : MD5(string: password!)) : ""
//        var dict: [String : Any]
//        if(username == nil || password == nil){
//        dict = ["signature": signature!, "pub_key": pub_key!, "epoch": epoch]
//        }else{
//        if(email == nil){
//        dict = ["username": username!, "password": derivedPassword, "epoch": epoch]
//        } else {
//        dict = ["username": username!, "password": derivedPassword, "email": email!, "epoch": epoch]
//        }
//        }
//        let msg = convertDictionaryToJSON(dict)
////        print("epoch: "+String(epoch))
//        /*let a = generateRandomString(length: 32)
//         print("a: "+a)
//         let b = generateRandomString(length: 32)
//         print("b: "+b)
//         let c = MD5(string: username)
//         print("c: "+c)
//         let key1 = MD5(string: (b+String(epoch)+a+c))
//         print("innerKey: "+(b+String(epoch)+a+c))
//         print("key1: "+key1)
//         let iv1 = String(MD5(string: String(epoch)).prefix(16))
//         print("iv1: "+iv1)
//         let aes1Cipher = AES(key: key1, iv: iv1)
//         let aes1 = (aes1Cipher?.encrypt(string: msg!))!
//         //    print("aes1: "+aes1)
//         */
//        let d = generateRandomString(length: 18) + MD5(string: String(epoch)) + generateRandomString(length: 18)
//        print("d: "+d)
//        let e = MD5(string: generateRandomString(length: 32))
////        print("e: "+e)
//        let key2 = MD5(string: "Ginger"+e+String(epoch)+d)
////        print("key2: "+key2)
//        let iv2 = String(MD5(string: MD5(string: d)).prefix(16))
////        print("iv2: "+iv2)
//        let aes2Cipher = AES(key: key2, iv: iv2)
//        let aes2 = aes2Cipher?.encrypt(string: msg!)!.base64EncodedString()
//        //    print("e-aes2-d: "+e+aes2!+d)
//        print("aes2: "+aes2!)
//        return e+aes2!+d
//        }
//
//        func decryptLogin(usernameOrPubKey: String, epoch: Int, msgEnc: String) -> String {
//        let key = MD5(string: usernameOrPubKey)
//        let iv = String(MD5(string: String(epoch)).prefix(16))
////        print(key)
////        print(iv)
//        let aes2Cipher = AES(key: key, iv: iv)
////        print("fffffffffffffffff")
////        print(msgEnc)
//        let aes2 = aes2Cipher?.decrypt(data: Data(hex: msgEnc))
////        print("aes2: "+aes2!)
//        return aes2!
//        }
//
//        func encryptGeneral(login_token: String, msg: String) -> String {
//        let key2 = MD5(string: "Shayna"+login_token)
//        let iv2 = String(login_token.prefix(16))
//        let aes2Cipher = AES(key: key2, iv: iv2)
//        let aes2 = aes2Cipher?.encrypt(string: msg)!.base64EncodedString()
//        return aes2!
//        }
//
//
//
//
//
