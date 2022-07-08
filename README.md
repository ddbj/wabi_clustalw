# wabi_clustalw
## 概要
ユーザーからのデータのPOSTに対してRequestIDを発行し、Univa Grid EngineでPOSTされたデータの処理を行うWeb APIのソースコード。
clustalw, vecscreen, mafft の3種類の処理をひとまとめにしてある。

vecscreen はGUI部分のソースコード (https://github.com/ddbj/vecscreen) が別にある。
実行する際は、本ソースコードをコンパイルしてできたwarファイル（wabi.war）とvecscreenのGUI部分（vecscreen.war）をともにtomcatにデプロイする。

## 依存ライブラリ
コンパイル時には事前に以下の3つの依存ライブラリをMavenでコンパイルしてローカルリポジトリに配置しておく。
Java6用とJava8用にそれぞれv6.0.3とv8.0.3のタグを付けてある（両者の違いはpom.xml中のコンパイラのバージョン指定が異なるだけ）ので、
必要なタグをチェックアウトしてコンパイルすること。

* https://github.com/ddbj/simpleutil
* https://github.com/ddbj/simpleutil-linux
* https://github.com/ddbj/simpleutil-numerical
