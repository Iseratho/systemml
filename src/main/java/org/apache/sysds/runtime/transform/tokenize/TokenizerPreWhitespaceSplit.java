/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.runtime.transform.tokenize;

import org.apache.sysds.common.Types;
import org.apache.sysds.runtime.matrix.data.FrameBlock;

import org.apache.wink.json4j.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TokenizerPreWhitespaceSplit implements TokenizerPre {

    private static final long serialVersionUID = 539127244034913364L;

    private final String splitRegex = "\\s+";

    private final List<Integer> idCols;
    private final int tokenizeCol;

    public TokenizerPreWhitespaceSplit(List<Integer> idCols, int tokenizeCol, JSONObject params) {
        this.idCols = idCols;
        this.tokenizeCol = tokenizeCol;
        // No configurable params yet
    }

    public List<Tokenizer.Token> splitToTokens(String text) {
        List<Tokenizer.Token> tokenList = new ArrayList<>();
        String[] textTokens = text.split(splitRegex);
        int curIndex = 0;
        for(String textToken: textTokens) {
            int tokenIndex = text.indexOf(textToken, curIndex);
            curIndex = tokenIndex;
            tokenList.add(new Tokenizer.Token(textToken, tokenIndex));
        }
        return tokenList;
    }

    @Override
    public List<Tokenizer.DocumentToTokens> tokenizePre(FrameBlock in) {
        List<Tokenizer.DocumentToTokens> documentsToTokenList = new ArrayList<>();

        Iterator<String[]> iterator = in.getStringRowIterator();
        iterator.forEachRemaining(s -> {
            // Convert index value to Java (0-based) from DML (1-based)
            String text = s[tokenizeCol - 1];
            List<Object> keys = new ArrayList<>();
            for (Integer idCol: idCols) {
                Object key = s[idCol.intValue() - 1];
                keys.add(key);
            }

            // Transform to Bag format internally
            List<Tokenizer.Token> tokenList = splitToTokens(text);
            documentsToTokenList.add(new Tokenizer.DocumentToTokens(keys, tokenList));
        });

        return documentsToTokenList;
    }
}
