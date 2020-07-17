package jp.ac.nig.ddbj.wabi.util;

import java.util.ResourceBundle;

public class Conf {
	/*
	 * Note: pom.xml 設定に応じて環境毎にリソースを切り替える。
	 * (pom.xml の project/profiles/profile/properties/resources.directory 要素を参照。)
	 * 例: 本番用は src/main/resources/env.properties
	 * 例: 統合テスト用は src/integration/resources/env.properties
	 * 例: 開発用は src/development/resources/env.properties
	 */
	private static ResourceBundle bundle = ResourceBundle.getBundle("env");
	
	/** このディレクトリの下にユーザーの計算結果などが置かれる。 */
	final static public String workingDirBase = bundle.getString("Conf.workingDirBase");
	// 例: workingDirBase = "/home/w3wabi/wabi/data/wabi-user-data/"
	
	/** この値がblastallのBLASTDB環境変数に設定される。*/
	final static public String blastDbPath    = bundle.getString("Conf.blastDbPath");
	// 例: blastDbPath = "/home/w3wabi/BLAST/blastdb/"

	/** この値がvecscreenのBLASTDB環境変数に設定される。*/
	final static public String vecscreenDbPath    = bundle.getString("Conf.vecscreenDbPath");
	// 例: blastDbPath = "/home/w3wabi/BLAST/blastdb/"

	/** このディレクトリの下にblastPngPerlが置かれる。 */
	final static public String pngPerlPath = bundle.getString("Conf.pngPerlPath");
	// 例: pngPerlPath = "/home/w3wabi/wabi/script/pngperl/"

	/** Webアプリ（blast）のURL。 */
	final static public String blastUrl = bundle.getString("Conf.blastUrl");
	// 例: blastUrl = "http://blast.ddbj.nig.ac.jp/"

	/** Webアプリ（clustalw）のURL。 */
	final static public String clustalwUrl = bundle.getString("Conf.clustalwUrl");
	// 例: blastUrl = "http://blast.ddbj.nig.ac.jp/"

	/** Webアプリ（vecscreen）のURL。 */
	final static public String vecscreenUrl = bundle.getString("Conf.vecscreenUrl");
	// 例: blastUrl = "http://blast.ddbj.nig.ac.jp/"

	/** qsub コマンドのオプション。 */
	final static public String qsubOptions = bundle.getString("Conf.qsubOptions");
	// 例: qsubOptions = "-l s_vmem=64G -l mem_req=64G -pe def_slot 1 -cwd -N"

}
