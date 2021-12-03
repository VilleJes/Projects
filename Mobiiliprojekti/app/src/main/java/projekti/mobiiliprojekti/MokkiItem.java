package projekti.mobiiliprojekti;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.Collections;
import java.util.List;

public class MokkiItem implements Parcelable{
    private String mMokkiImage;
    private String mOtsikko;
    private int mHinta;
    private String mOsoite;
    private String mHuoneMaara;
    private int mNelioMaara;
    private String mLammitys;
    private String mVesi;
    private String mSauna;
    private String mKuvaus;
    private String mOtsikkoID;
    private String mVuokraaja;
    private String mVuokraajaID;
    private String mKey;
    private String mDates;
    //private String mOmistaja;

    public MokkiItem()
    {

    }

    public MokkiItem(String MokkiImage, String otsikko, int hinta, String osoite, String huoneMaara,
                     int nelioMaara, String lammitys, String vesi, String sauna, String kuvaus, String otsikkoID,
                     String vuokraaja, String vuokraajaID, String dates)
    {
        mMokkiImage = MokkiImage;
        mOtsikko = otsikko;
        mHinta = hinta;
        mOsoite = osoite;
        mHuoneMaara = huoneMaara;
        mNelioMaara = nelioMaara;
        mLammitys = lammitys;
        mVesi = vesi;
        mSauna = sauna;
        mKuvaus = kuvaus;
        mOtsikkoID = otsikkoID;
        mVuokraaja = vuokraaja;
        mVuokraajaID = vuokraajaID;
        mDates = dates;
        //mOmistaja = omistaja;
    }

    protected MokkiItem(Parcel in) {
        mMokkiImage = in.readString();
        mOtsikko = in.readString();
        mHinta = in.readInt();
        mOsoite = in.readString();
        mHuoneMaara = in.readString();
        mNelioMaara = in.readInt();
        mLammitys = in.readString();
        mVesi = in.readString();
        mSauna = in.readString();
        mKuvaus = in.readString();
        mOtsikkoID = in.readString();
        mVuokraaja = in.readString();
        mVuokraajaID = in.readString();
        mDates = in.readString();
        //mOmistaja = in.readString();
    }

    public static final Creator<MokkiItem> CREATOR = new Creator<MokkiItem>() {
        @Override
        public MokkiItem createFromParcel(Parcel in) {
            return new MokkiItem(in);
        }

        @Override
        public MokkiItem[] newArray(int size) {
            return new MokkiItem[size];
        }
    };

    public String getMokkiImage() { return mMokkiImage; }
    public String getOtsikko() { return mOtsikko; }
    public int getHinta() { return mHinta; }
    public String getOsoite() { return mOsoite; }
    public String getHuoneMaara() { return mHuoneMaara; }
    public int getNelioMaara() { return mNelioMaara; }
    public String getLammitys() { return mLammitys; }
    public String getVesi() { return mVesi; }
    public String getSauna() { return mSauna; }
    public String getKuvaus() { return mKuvaus; }
    public String getOtsikkoID() { return mOtsikkoID; }
    public String getVuokraaja() { return mVuokraaja; }
    public String getVuokraajaID() { return mVuokraajaID; }
    public String getmDates() { return mDates; }
    //public String getOmistaja() { return mOmistaja; }


    public void setMokkiImage(String mokkiImage) { this.mMokkiImage = mokkiImage; }
    public void setOtsikko(String otsikko ) { this.mOtsikko = otsikko; }
    public void setHinta(int hinta ) { this.mHinta = hinta; }
    public void setOsoite(String osoite ) { this.mOsoite = osoite; }
    public void setHuoneMaara(String huoneMaara ) { this.mHuoneMaara = huoneMaara; }
    public void setNelioMaara(int nelioMaara ) { this.mNelioMaara = nelioMaara; }
    public void setLammitys(String lammitys ) { this.mLammitys = lammitys; }
    public void setVesi(String vesi ) { this.mVesi = vesi; }
    public void setSauna(String sauna ) { this.mSauna = sauna; }
    public void setKuvaus(String kuvaus ) { this.mKuvaus = kuvaus; }
    public void setOtsikkoID(String otsikkoID ) { this.mOtsikkoID = otsikkoID; }
    public void setVuokraaja(String vuokraaja ) { this.mVuokraaja = vuokraaja; }
    public void setVuokraajaID(String vuokraajaID) { this.mVuokraajaID = vuokraajaID; }
    public void setDates(String dates) { this.mDates = dates; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMokkiImage);
        dest.writeString(mOtsikko);
        dest.writeInt(mHinta);
        dest.writeString(mOsoite);
        dest.writeString(mHuoneMaara);
        dest.writeInt(mNelioMaara);
        dest.writeString(mLammitys);
        dest.writeString(mVesi);
        dest.writeString(mSauna);
        dest.writeString(mKuvaus);
        dest.writeString(mOtsikkoID);
        dest.writeString(mVuokraaja);
        dest.writeString(mVuokraajaID);
        dest.writeString(mDates);
        //dest.writeString(mOmistaja);
    }

    @Exclude
    public String getKey() { return mKey; }
    @Exclude
    public void setKey(String key) { mKey = key; }
}
