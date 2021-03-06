/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.director.drools.testgen;

import org.kie.api.runtime.KieContainer;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirectorFactory;

public class TestGenDroolsScoreDirectorFactory<Solution_> extends DroolsScoreDirectorFactory<Solution_> {

    public TestGenDroolsScoreDirectorFactory(KieContainer kieContainer, String ksessionName) {
        super(kieContainer, ksessionName);
    }

    @Override
    public DroolsScoreDirector<Solution_> buildScoreDirector(boolean constraintMatchEnabledPreference) {
        return new TestGenDroolsScoreDirector<>(this, constraintMatchEnabledPreference);
    }

}
