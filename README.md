# Edit+プラグイン

## 説明
astah*の図の要素をちょこっと編集できるプラグインです。
反転、回転、選択など、標準機能にはないけど、よくある操作を簡単に実現できます。

# ダウンロード
- [ここ](https://github.com/snytng/editplus/releases/download/V0.1.4/editplus-0.1.4.jar)からダウンロードして下さい。

# インストール
- ダウンロードしたプラグインファイルをastah*アプリケーションにドラッグドロップするか、Program Files\asta-professionals\pluginsに置いて下さい。

# 機能説明
（関連編集）　括弧はニーモニックです。Altと一緒に押すとその機能を実行します。
- `◀ ▶(N)`：関連名の方向を反転します。
- `--x--(U)`: 集約・コンポジションをなくします。（単純な関連にします）
- `◇◆-◆◇(I)`：集約・コンポジションを追加します。押すたびに左右と種類が変更します。
- `--x--(J)`: 集約・コンポジションをなくします。（単純な関連にします）
- `m----(K)`: 左側の多重度を、1→0..1→0..\*→\*→1..\*→なし、と順番に変えていきます。
- `----m(L)`: 右側の多重度を、1→0..1→0..\*→\*→1..\*→なし、と順番に変えていきます。
- `--x--(M)`: 誘導可能性をなくします。（単純な関連にします）
- `<----(,)`: 左側の誘導可能性を、誘導可能、誘導不可能、誘導可能性未定、と順番に変えていきます。
- `---->(.)`: 右側の誘導可能性を、誘導可能、誘導不可能、誘導可能性未定、と順番に変えていきます。
- `◁---▷(G)`: 継承の方向を反転します。
- `<--->(B)`: 依存の方向を反転します。

（要素編集）
- `上下反転`：選択した要素の位置関係を上下反対にします
- `左右反転`：選択した要素の位置関係を左右反対にします
- `右90度回転`：選択した要素を右90度回転します。
- `クラス選択`：選択した要素の中のクラスだけを選択します。線やノートなど他要素と一緒に選択しているときにクラス以外の選択を解除します。
- `関連名整列`：関連名の位置を関連の真ん中に移動します。
- `色スポイト`：選択した要素の色を変更します。色を変更したい要素を選択してから、色スポイトボタンを押し、変更したい色を持つ要素を選択すると、色が変更されます。
- `ノート色反映`：すべての要素をノートの色に合わせて変更します。ノートと要素を選択したあとに押してください。
- `ステレオタイプ追加`：ノートの文字列と同じステレオタイプを追加します。ステレオタイプ文字列を書いたノートと要素を選択したあとに押してください。すでにステレオタイプがある場合には追加しません。
- `ステレオタイプ削除`：ノートの文字列と同じステレオタイプを削除します。ステレオタイプ文字列を書いたノートと要素を選択したあとに押してください。

(センタリング)
- `><(C)`：選択した要素が図の中心になるように表示します。

## 使い方
- 操作したい図を開く
- Edit+プラグインタブを押してプラグイン画面を表示する（表示している状態の操作が有効になります）
- 編集したい要素を選択する
- 実行したい`機能ボタン`を押す
    - 関連編集は要素を一つだけ選択しているときしか実行できません（ボタンが有効になりません）

## 変更履歴
- V0.1.4
  - 色スポイトをastah* APIで編集できる図・要素でも利用できるように機能拡張。
  - リンクの反転や回転ができない不具合を修正。  
- V0.1.3
  - 反転や回転の重心位置の計算での不具合を修正。

以上
