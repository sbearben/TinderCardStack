# TinderCardStack
Custom RecyclerView/LayoutManager to implement a Tinder-like swipeable card stack. Based on CardStackView which can be found here: https://github.com/yuyakaido/CardStackView

### Major Changes from CardStackView

* Written in Kotlin
* Uses a [FlingListener](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.OnFlingListener) as opposed to [SnapHelper](https://developer.android.com/reference/android/support/v7/widget/SnapHelper). This eliminted and simplified a lot of code, and is more in line with the actions that are being performed.
SnapHelper wasn't being used correctly and FlingListener offered the proper solution.
* Proper recycling of unused views
