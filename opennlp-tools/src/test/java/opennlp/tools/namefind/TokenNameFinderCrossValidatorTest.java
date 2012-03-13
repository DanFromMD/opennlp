/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.tools.namefind;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Map;

import opennlp.tools.cmdline.namefind.NameEvaluationErrorListener;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelType;
import opennlp.tools.util.model.ModelUtil;

import org.junit.Test;

public class TokenNameFinderCrossValidatorTest {

  private final String TYPE = "default";

  @Test
  /**
   * Test that reproduces jira OPENNLP-463
   */
  public void testWithNullResources() throws Exception {

    FileInputStream sampleDataIn = new FileInputStream(getClass()
        .getClassLoader()
        .getResource("opennlp/tools/namefind/AnnotatedSentences.txt").getFile());
    ObjectStream<NameSample> sampleStream = new NameSampleDataStream(
        new PlainTextByLineStream(sampleDataIn.getChannel(), "ISO-8859-1"));

    TrainingParameters mlParams = ModelUtil.createTrainingParameters(70, 1);
    mlParams.put(TrainingParameters.ALGORITHM_PARAM,
        ModelType.MAXENT.toString());

    TokenNameFinderCrossValidator cv = new TokenNameFinderCrossValidator("en",
        TYPE, mlParams, null, null);

    cv.evaluate(sampleStream, 2);

    assertNotNull(cv.getFMeasure());
  }
  
  @Test
  /**
   * Test that tries to reproduce jira OPENNLP-466
   */
  public void testWithNameEvaluationErrorListener() throws Exception {

    FileInputStream sampleDataIn = new FileInputStream(getClass()
        .getClassLoader()
        .getResource("opennlp/tools/namefind/AnnotatedSentences.txt").getFile());
    ObjectStream<NameSample> sampleStream = new NameSampleDataStream(
        new PlainTextByLineStream(sampleDataIn.getChannel(), "ISO-8859-1"));

    TrainingParameters mlParams = ModelUtil.createTrainingParameters(70, 1);
    mlParams.put(TrainingParameters.ALGORITHM_PARAM,
        ModelType.MAXENT.toString());
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    NameEvaluationErrorListener listener = new NameEvaluationErrorListener(out); 

    Map<String, Object> resources = Collections.emptyMap();
    TokenNameFinderCrossValidator cv = new TokenNameFinderCrossValidator("en",
        TYPE, mlParams, null, resources, listener);

    cv.evaluate(sampleStream, 2);
    
    assertTrue(out.size() > 0);
    assertNotNull(cv.getFMeasure());
  }
}
