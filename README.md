# Palette
It's a flexible ui framework based on MVVM concept.

MVP介绍</br>
	在客户端的开发中，为了逻辑上的解耦，我们通常希望将数据层(M)、界面层(V)、控制逻辑(C)进行分离，也即我们通常所说的MVC模式，MVC模式的优点是三层逻辑之间是解耦的，每一层都可以单独的开发和测试。</br>
	Android中Activity/Fragment充当了Controller的角色，但由于Activity是一个比较大的组件，他兼具着处理界面(例如ActionBar)、处理各类系统事件(生命周期、消息等)，实际使用中如果还要把整个界面的控制逻辑写在Activity中，就会使得Activity显得非常的臃肿。此时，我们单独把Controller中与界面展现相关的代码剥离出来，于是产生了Presenter。Presenter单纯只用来控制界面显示，它是Activity/Fragment(Controller)的一个部分。</br>
场景1：用户详情界面</br>
	Presenter会被用来将User对象展示到一个View上，它会交由Activity管理，同时Activity还负责处理横竖屏变化、打Log、处理ActivityResult等内容。</br>
场景2：用户列表界面</br>
	Presenter会被用来将一个User展示到ListView的一个ItemView(ViewHolder)上，Fragment通过Adapter控制Presenter。
MVPs概念</br>
  以上是传统的MVP模式，其基本实现了分层的目的。但在客户端逐渐发展成为一个更为复杂的系统时，我们发现由于卡片开始包含更多的功能，例如一张卡片上可能展示视频、多张图片、附件、各种Span等等，此时这张卡片对应的Presenter变的非常的臃肿。随之带来的是这个Presenter的可复用性大幅下降，某些可能只有少量不用的卡片(例如没有视频展示)，只能通过继承原先的Presetner来进行改造，由于Java不能支持多继承，导致代码被迫越写约耦合，如此反复，使得整个工程开发和维护成本大幅提高。</br>
  MVPs模式是对MVP的改进而非新的设计，其目标是提高Presenter的复用程度。</br>
  #未完待续</br>
