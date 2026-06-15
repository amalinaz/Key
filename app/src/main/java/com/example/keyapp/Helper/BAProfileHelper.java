package com.example.keyapp.Helper;

import android.location.Location;
import androidx.annotation.NonNull;
import com.example.keyapp.Models.BAprofile;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BAProfileHelper {

    public static List<BAprofile> rankProviders(List<BAprofile> providers) {
        double wRating = 0.3125;
        double wDistance = 0.25;
        double wExperience = 0.25;
        double wPrice = 0.1875;

        double maxRating = providers.stream().mapToDouble(p -> p.getRating()).max().orElse(1);
        double minDistance = providers.stream().mapToDouble(p -> p.getDistance()).min().orElse(1);
        double maxExp = providers.stream().mapToDouble(p -> p.getExperience()).max().orElse(1);
        double minPrice = providers.stream().mapToDouble(p -> p.getMinPrice()).min().orElse(1);

        for (BAprofile p : providers) {
            double normRating = p.getRating()/maxRating; // benefit
            double normDistance = minDistance/p.getDistance(); //cost
            double normExp = p.getExperience()/maxExp; //benefit
            double normPrice = (double)minPrice/p.getMinPrice(); //cost

            double score = normRating*wRating + normDistance*wDistance + normExp*wExperience + normPrice*wPrice;
            p.setScore(score);
        }

        Collections.sort(providers, (a,b) -> Double.compare(b.getScore(), a.getScore()));
        return providers;
    }
}