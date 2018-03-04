/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.apache.poi.xssf.util;

import java.io.InputStream;

import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;
import org.apache.poi.util.ReplacingInputStream;

/**
 * This is a seriously sick fix for the fact that some .xlsx
 *  files contain raw bits of HTML, without being escaped
 *  or properly turned into XML.
 * The result is that they contain things like &gt;br&lt;,
 *  which breaks the XML parsing.
 * This very sick InputStream wrapper attempts to spot
 *  these go past, and fix them.
 * Only works for UTF-8 and US-ASCII based streams!
 * It should only be used where experience shows the problem
 *  can occur...
 *  
 *  @deprecated 3.16-beta2 - use ReplacingInputStream(source, "&gt;br&lt;", "&gt;br/&lt;")
 */
@Deprecated
@Removal(version="3.18")
@Internal
public class EvilUnclosedBRFixingInputStream extends ReplacingInputStream {
   public EvilUnclosedBRFixingInputStream(InputStream source) {
      super(source, "<br>", "<br/>");
   }
}
