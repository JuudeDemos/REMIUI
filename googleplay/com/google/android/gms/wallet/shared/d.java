package com.google.android.gms.wallet.shared;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.wallet.instrumentmanager.R;

public class d implements Creator<LegalDocsForCountry> {
    static void a(LegalDocsForCountry legalDocsForCountry, Parcel parcel, int i) {
        int bU = b.bU(parcel);
        b.c(parcel, 1, legalDocsForCountry.mVersionCode);
        b.a(parcel, 2, legalDocsForCountry.aVH, false);
        b.a(parcel, 3, legalDocsForCountry.aVI, false);
        b.a(parcel, 4, legalDocsForCountry.aVJ, false);
        b.J(parcel, bU);
    }

    public /* synthetic */ Object createFromParcel(Parcel x0) {
        return it(x0);
    }

    public LegalDocsForCountry it(Parcel parcel) {
        String[] strArr = null;
        int bT = a.bT(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        while (parcel.dataPosition() < bT) {
            int bS = a.bS(parcel);
            switch (a.dk(bS)) {
                case R.styleable.WalletImFormEditText_validatorErrorString /*1*/:
                    i = a.g(parcel, bS);
                    break;
                case R.styleable.WalletImFormEditText_validatorRegexp /*2*/:
                    str2 = a.p(parcel, bS);
                    break;
                case R.styleable.WalletImFormEditText_requiredErrorString /*3*/:
                    str = a.p(parcel, bS);
                    break;
                case R.styleable.WalletImFormEditText_required /*4*/:
                    strArr = a.B(parcel, bS);
                    break;
                default:
                    a.b(parcel, bS);
                    break;
            }
        }
        if (parcel.dataPosition() == bT) {
            return new LegalDocsForCountry(i, str2, str, strArr);
        }
        throw new a.a("Overread allowed size end=" + bT, parcel);
    }

    public LegalDocsForCountry[] ls(int i) {
        return new LegalDocsForCountry[i];
    }

    public /* synthetic */ Object[] newArray(int x0) {
        return ls(x0);
    }
}
