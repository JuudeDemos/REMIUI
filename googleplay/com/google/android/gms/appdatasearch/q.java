package com.google.android.gms.appdatasearch;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.wallet.instrumentmanager.R;

public class q implements Creator<NativeApiInfo> {
    static void a(NativeApiInfo nativeApiInfo, Parcel parcel, int i) {
        int bU = b.bU(parcel);
        b.a(parcel, 1, nativeApiInfo.sharedLibAbsoluteFilename, false);
        b.c(parcel, 1000, nativeApiInfo.mVersionCode);
        b.a(parcel, 2, nativeApiInfo.sharedLibExtensionAbsoluteFilename, false);
        b.a(parcel, 3, nativeApiInfo.downloadManagerFilename, false);
        b.J(parcel, bU);
    }

    public NativeApiInfo A(Parcel parcel) {
        String str = null;
        int bT = a.bT(parcel);
        int i = 0;
        String str2 = null;
        String str3 = null;
        while (parcel.dataPosition() < bT) {
            int bS = a.bS(parcel);
            switch (a.dk(bS)) {
                case R.styleable.WalletImFormEditText_validatorErrorString /*1*/:
                    str3 = a.p(parcel, bS);
                    break;
                case R.styleable.WalletImFormEditText_validatorRegexp /*2*/:
                    str2 = a.p(parcel, bS);
                    break;
                case R.styleable.WalletImFormEditText_requiredErrorString /*3*/:
                    str = a.p(parcel, bS);
                    break;
                case 1000:
                    i = a.g(parcel, bS);
                    break;
                default:
                    a.b(parcel, bS);
                    break;
            }
        }
        if (parcel.dataPosition() == bT) {
            return new NativeApiInfo(i, str3, str2, str);
        }
        throw new a.a("Overread allowed size end=" + bT, parcel);
    }

    public NativeApiInfo[] U(int i) {
        return new NativeApiInfo[i];
    }

    public /* synthetic */ Object createFromParcel(Parcel x0) {
        return A(x0);
    }

    public /* synthetic */ Object[] newArray(int x0) {
        return U(x0);
    }
}
