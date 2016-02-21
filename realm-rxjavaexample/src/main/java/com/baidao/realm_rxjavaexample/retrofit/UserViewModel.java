/*
 * Copyright 2015 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidao.realm_rxjavaexample.retrofit;

public class UserViewModel {

    private final String username;
    private final int publicRepos;
    private final int publicGists;

    public UserViewModel(String username, int publicRepos, int publicGists) {
        this.username = username;
        this.publicRepos = publicRepos;
        this.publicGists = publicGists;
    }

    public String getUsername() {
        return username;
    }

    public int getPublicRepos() {
        return publicRepos;
    }

    public int getPublicGists() {
        return publicGists;
    }
}
