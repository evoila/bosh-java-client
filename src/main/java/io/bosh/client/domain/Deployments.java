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

import io.bosh.client.v2.deployments.Deployment;

import java.util.List;

/**
 * @author David Ehringer (n0119737)
 */
public class Deployments {

    private List<Deployment> deployments;

    public List<Deployment> getDeployments() {
        return deployments;
    }
    
}
