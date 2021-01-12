/*
 * Copyright 2021. gudaoxuri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {jsBuild, jvmBuild, jvmPrepare} from "./src";

const gulp = require("gulp")
const {series} = require('gulp')
const glob = require('glob');
const browserify = require("browserify")
const source = require('vinyl-source-stream')
const buffer = require('vinyl-buffer')
const uglify = require("gulp-uglify-es").default
const rm = require('rimraf')
let ts = require("gulp-typescript")
let tsProject = ts.createProject('tsconfig.test.json')

const _path = {
    dist: './test_dist',
    action: './test_dist/test/actions',
}

const _clean = (done) => {
    rm(_path.dist, error => {
        if (error) throw error
        done()
    })
}

const _ts = () => {
    return tsProject.src()
        .pipe(tsProject()).js
        .pipe(gulp.dest(_path.dist))
        .pipe(jvmPrepare(_path.action))
}

function _jvmBuild() {
    return browserify({
        entries: glob.sync(_path.action+'/JVM.js'),
        standalone: "JVM"
    })
        .bundle()
        .pipe(source('test.js'))
        .pipe(buffer())
        /*.pipe(uglify())*/
        .pipe(gulp.dest('../../../module/task/src/test/resources/'))
        /*.pipe(jvmBuild())*/
}


module.exports = {
    test: series(_clean, _ts, _jvmBuild),
}