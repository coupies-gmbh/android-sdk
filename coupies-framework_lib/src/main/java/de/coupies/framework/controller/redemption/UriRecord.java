/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.coupies.framework.controller.redemption;

import java.nio.charset.Charset;

import android.net.Uri;
import android.nfc.NdefRecord;


/**
 * A parsed record containing a Uri.
 */
public class UriRecord implements ParsedNdefRecord {

    @SuppressWarnings("unused")
	private static final String TAG = "UriRecord";

    public static final String RECORD_TYPE = "UriRecord";

    private final Uri mUri;

    private UriRecord(Uri uri) {
       this.mUri = uri;
    }

	public Uri getUri() {
        return mUri;
    }

    /**
     * Convert {@link android.nfc.NdefRecord} into a {@link android.net.Uri}.
     * This will handle both TNF_WELL_KNOWN / RTD_URI and TNF_ABSOLUTE_URI.
     *
     * @throws IllegalArgumentException if the NdefRecord is not a record
     *         containing a URI.
     */
    public static UriRecord parse(NdefRecord record) {
        short tnf = record.getTnf();
        if (tnf == NdefRecord.TNF_WELL_KNOWN) {
            return parseWellKnown(record);
        } else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
            return parseAbsolute(record);
        }
        throw new IllegalArgumentException("Unknown TNF " + tnf);
    }

    /** Parse and absolute URI record */
    private static UriRecord parseAbsolute(NdefRecord record) {
        byte[] payload = record.getPayload();
        Uri uri = Uri.parse(new String(payload, Charset.forName("UTF-8")));
        return new UriRecord(uri);
    }

    /** Parse an well known URI record */
    private static UriRecord parseWellKnown(NdefRecord record) {
         byte[] payload = record.getPayload();

        String prefix = Byte.toString(payload[0]);
        System.out.println(prefix);
        // Hier kann noch ein Test rein um zu schauen das es in jedem fall auch ein richtiger Record ist!!
        
        String fullUri = new String(payload, Charset.forName("UTF-8"));
       
        Uri uri = Uri.parse(fullUri);
        return new UriRecord(uri);
    }

    public static boolean isUri(NdefRecord record) {
        try {
            parse(record);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @SuppressWarnings("unused")
	private static final byte[] EMPTY = new byte[0];
}