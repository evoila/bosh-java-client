/*
 * Copyright 2015 the original author or authors.
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
package io.bosh.client.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author David Ehringer
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StemcellDetails {

    private String name;
    private String version;
    private String cid;
    private List<DeploymentName> deployments = new ArrayList<DeploymentName>();

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getCid() {
        return cid;
    }

    public List<String> getDeployments() {
        return deployments.stream().map(d -> d.getName()).collect(toList());
    }

    @Override
    public String toString() {
        return "StemcellDetails [name=" + name + ", version=" + version + ", cid=" + cid
                + ", deployments=" + getDeployments() + "]";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class DeploymentName {

        private String name;

        public String getName() {
            return name;
        }

    }

}
