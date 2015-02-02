#Android BounceProgressBar Widget
----


##Screenshots
----
![image](https://raw.githubusercontent.com/zhengxiaopeng/BounceProgressBar/master/screenshots/bounce.gif)

##Usage
----
Declare an BounceProgressBar inside your XML layout file.

```xml
<org.roc.bounceprogressbar.BounceProgressBar
            xmlns:bpb="http://schemas.android.com/apk/res-auto"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerHorizontal="true"
            android:layout_centerVertical="true"
            bpb:shape="circle"
            bpb:singleSrcSize="8dp"
            bpb:speed="250"
            bpb:src="#6495ED" />
```

or

```xml
<org.roc.bounceprogressbar.BounceProgressBar
    		xmlns:bpb="http://schemas.android.com/apk/res-auto"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerHorizontal="true"
            android:layout_centerVertical="true"
            bpb:shape="heart"
            bpb:singleSrcSize="25dp"
            bpb:speed="300"
            bpb:src="@drawable/github" />
```

####The BounceProgressBar has the following attribute
```xml
    <declare-styleable name="BounceProgressBar">

        <!-- the single child size -->
        <attr name="singleSrcSize" format="dimension" />
        <!-- the bounce animation one-way duration -->
        <attr name="speed" format="integer" />
        <!-- the child count -->
        <!-- <attr name="count" format="integer" min="1" /> -->
        <!-- the progress child shape -->
        <attr name="shape" format="enum">
            <enum name="original" value="0" />
            <enum name="circle" value="1" />
            <enum name="pentagon" value="2" />
            <enum name="rhombus" value="3" />
            <enum name="heart" value="4" />
        </attr>
        <!-- the progress drawable resource -->
        <attr name="src" format="reference|color"></attr>
    </declare-styleable>
```

##More
----
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-BounceProgressBar-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1221)

More detail please see blog: [http://blog.csdn.net/bbld_/article/details/41246247](http://blog.csdn.net/bbld_/article/details/41246247 "csdn blog")

##License
-------

```
Copyright 2014 Rocko.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
