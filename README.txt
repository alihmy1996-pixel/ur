# حسابات أور – مشروع Android

هذا مشروع Android Studio يغلّف صفحة HTML المرفقة داخل WebView محلياً، ويستخدم الشعار المرفق.

## البناء
1. افتح Android Studio.
2. اختر Open وافتح مجلد `hesabat_oor_android`.
3. انتظر Gradle Sync وثبّت Android SDK 35 عند الطلب.
4. من Build اختر Build Bundle(s) / APK(s) ثم Build APK(s).
5. ستجد APK داخل `app/build/outputs/apk/debug/`.

## ملاحظات مهمة عن العمل دون إنترنت
- الصفحة تفتح محلياً ولا تحتاج اتصالاً بالإنترنت.
- بيانات الصفحة محفوظة محلياً عبر localStorage في WebView.
- أزلت تحميل خط Google الخارجي ومكتبة Excel الخارجية كي لا تطلب الصفحة الإنترنت.
- وظائف Excel (استيراد/تصدير XLSX) ستحتاج تضمين مكتبة Excel محلية لاحقاً؛ بقية سجل الأقساط والوصولات والنسخ الاحتياطي/الطباعة تعمل حسب إمكانات Android WebView.
