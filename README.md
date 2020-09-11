# wabi_clustalw
## 概要
ユーザーからのデータのPOSTに対してRequestIDを発行し、Univa Grid Engineでデータの処理を行うWeb APIのソースファイル。
clustalw, vecscreen, mafft の3種類の処理をひとまとめにしてある。

vecscreen はGUI部分のソースコード (https://gitlab.ddbj.nig.ac.jp/yookuda/vecscreen) が別にある。

## 依存ライブラリ
コンパイル時には事前に以下の3つの依存ライブラリをMavenでコンパイルしてローカルリポジトリに配置しておく。
Java6用とJava8用にそれぞれv6.0.3とv8.0.3のタグを付けてある（両者の違いはpom.xml中のコンパイラのバージョン指定が異なるだけ）ので、
必要なタグをチェックアウトしてコンパイルすること。

* https://gitlab.ddbj.nig.ac.jp/yookuda/simpleutil
* https://gitlab.ddbj.nig.ac.jp/yookuda/simpleutil-linux
* https://gitlab.ddbj.nig.ac.jp/yookuda/simpleutil-numerical