
![Dialog: app for hear impaired people](https://raw.githubusercontent.com/jookovjook/dialog/master/images/Art.png)

App for hear impaired people.

- [Introduction](#introduction)
- [Features](#features)
- [Requirements](#requirements)
- [Communication](#communication)
- [Installation](#installation)
- [Overview](#overview)
    - [Cards](#cards)
    - [Dialog](#dialog)
    - [Split Screen](#split-screen) 
    - [Supported Languages](#supported-languages)
- [Credits](#credits)
- [Donations](#donations)
- [License](#license)

## Introduction


Dialog is an Android app which helps hear impaired and deaf people to communicate with people without hearing problems.
Dialog uses Yandex SpeechKit API.

## Features

- [x] Speech-to-text recognition
- [x] Speech synthesizing
- [x] Creating and editing quick access phrases to synthesize loudly
- [x] Conducting a dialog with person without hearing problems: recognition of his/her speech and synthesizing of inserted by user text
- [x] Simultanious recognition and synthesizing

## Requirements

- Android 4.2 +

## Communication

- If you **need help**, use [Stack Overflow](http://stackoverflow.com/questions/tagged/jookovjook). (Tag 'jookovjook')
- If you'd like to **ask a general question**, use [Stack Overflow](http://stackoverflow.com/questions/tagged/jokovjook).
- If you **found a bug**, open an issue.
- If you **have a feature request**, open an issue.
- If you **want to contribute**, submit a pull request.

## Installation

Firstly clone [dialog](https://github.com/jookovjook/dialog) respository

```bash
$ git clone https://github.com/jookovjook/dialog
```

Add Yandex SpeechKit API key in `Config.java`:

```Java
public static final String API_KEY = "YOUR_API_KEY";
```

If you don't have an API key, you can get one at [Yandex SpeeckKit](https://tech.yandex.ru/speechkit/).

`Build` project and `Run` it.

## Overview

### Cards

• Use cards on the main screen to quickly synthesize text

<img src="https://raw.githubusercontent.com/jookovjook/dialog/master/images/1.png" width="250">

You can create your own card by pressing `+` on the main screen and edit already existing cards.

### Dialog

Type text using keyboard to enable other people see what you want to say them.

Type `Microphone` button at the top of the screen to start speech-to text recognition. Recongised speech will be quickly displayed at the screen.

<img src="https://raw.githubusercontent.com/jookovjook/dialog/master/images/2.png" width="250">

### Split Screen

Use Split Screen mode to simultaneous recognition and synthesizing.

<img src="https://raw.githubusercontent.com/jookovjook/dialog/master/images/3.png" width="250">

### Supported Languages

Dialog now supports `Russian`, `English`, `Turkish` and `Ukrainian` languages.

<img src="https://raw.githubusercontent.com/jookovjook/dialog/master/images/4.png" width="250">

## Credits

Created by [jookovjook](https://github.com/jookovjook).

[vk.com/jookovjook](https://vk.com/jookovjook)

[fb.com/jookovjook](https://fb.com/jookovjook)

[t.me/jookovjook](https://t.me/jookovjook)
    
You are welcome to participate the project!

## Donations

I'll be gratefull if you donate some funds to my `Etherium wallet`:

```
0x9B9a7B954E4c634b200Be98aa602b7ee9006b05B
```

## License

Dialog is released under the Apache 2.0 license.

    Copyright 2018 JookovJook
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
