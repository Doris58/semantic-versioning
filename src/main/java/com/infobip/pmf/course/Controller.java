package com.infobip.pmf.course;

import org.springframework.web.bind.annotation.*;

import java.util.regex.*;

@RestController
public class Controller 
{
    @GetMapping("/versions/max")
    public String maxVersion(@RequestParam(value = "v1", defaultValue = "") String v1, @RequestParam(value = "v2", defaultValue = "") String v2)
    {
        if(v1.isEmpty() || v2.isEmpty())
            return "Unesite sve potrebne parametre.\n";

        boolean ok = provjeraVerzije(v1);
        if(!ok)
            return "Dana semanticka verzija nije valjana. Proucite detaljno dozvoljene oblike na: https://semver.org/spec/v2.0.0.html \n";

        ok = provjeraVerzije(v2);
        if(!ok)
            return "Dana semanticka verzija nije valjana. Proucite detaljno dozvoljene oblike na: https://semver.org/spec/v2.0.0.html \n";

        String v1_sub, v2_sub;

        int from1, to1 = 0, from2, to2 = 0;

        // usporedba major i minor
        for(int i = 1; i <= 2; i++)
        {
            from1 = to1;
            while(v1.charAt(to1) != '.')
                to1++;
            v1_sub = v1.substring(from1, to1);

            from2 = to2;
            while(v2.charAt(to2) != '.')
                to2++;
            v2_sub = v2.substring(from2, to2);

            if(Integer.parseInt(v1_sub) > Integer.parseInt(v2_sub))
                return v1  + "\n";
            if(Integer.parseInt(v1_sub) < Integer.parseInt(v2_sub))
                return v2  + "\n";

            to1++;
            to2++;
        }

        // --- usporedba patch --- //////////////

        from1 = to1;
        /* PRETPOSTAVLJAM SHORT-CIRCUIT ! */
        while(to1 != v1.length() && v1.charAt(to1) != '-' && v1.charAt(to1) != '+')
            to1++;
        v1_sub = v1.substring(from1, to1);

        from2 = to2;
        while(to2 != v2.length() && v2.charAt(to2) != '-' && v2.charAt(to2) != '+')
            to2++;
        v2_sub = v2.substring(from2, to2);

        if(Integer.parseInt(v1_sub) > Integer.parseInt(v2_sub))
            return v1 + "\n";;
        if(Integer.parseInt(v1_sub) < Integer.parseInt(v2_sub))
            return v2 + "\n";;

        // ako v1 nema pre-lease dio
        /* PRETPOSTAVLJAM SHORT-CIRCUIT ! */
        if(to1 == v1.length() || v1.charAt(to1) == '+')
            return v1 + "\n";

        //  ako v2 nema pre-release dio
        if(to2 == v2.length() || v2.charAt(to2) == '+')
            return v2 + "\n";

        // --- obje imaju pre-release dio --- /////////////////////

        String num_regex;
        Pattern pattern;
        Matcher matcher;
        int comp;

        to1++; to2++;
        while(true)
        {   // usporedba dijelova pre-releasea redom

            from1 = to1;
            while(to1 != v1.length() && v1.charAt(to1) != '.' && v1.charAt(to1) != '+')
                to1++;
            v1_sub = v1.substring(from1, to1);

            from2 = to2;
            while(to2 != v2.length() && v2.charAt(to2) != '.' && v2.charAt(to2) != '+')
                to2++;
            v2_sub = v2.substring(from2, to2);


            num_regex = "0|([1-9]\\d*)";
            pattern = Pattern.compile(num_regex);
            matcher = pattern.matcher(v1_sub);
            if(matcher.matches())
            {
                // v1_sub je numericki
                matcher = pattern.matcher(v2_sub);
                if(matcher.matches())
                {
                    //oba su numericki
                    {
                        if(Integer.parseInt(v1_sub) > Integer.parseInt(v2_sub))
                            return v1 + "\n";
                        if(Integer.parseInt(v1_sub) < Integer.parseInt(v2_sub))
                            return v2 + "\n";
                    }
                }
                else //v1_sub je numericki, a v2_sub nije numericki
                    return v2 + "\n";
            }
            else // v1_sub nije numericki
            {
                matcher = pattern.matcher(v2_sub);
                if(matcher.matches())
                {
                    // v2_sub je numericki
                    return v1 + "\n";
                }
            }

            // ---- oba nisu numericki --- /////////

            comp = v1_sub.compareTo(v2_sub);
            if(comp < 0)
                return v2 + "\n";
            if(comp > 0)
                return v1 + "\n";

            // ---- dosad su jednaki --- /////

            // zavrsio pre-relase dio od v1, a dosad su jednake
            if(to1 == v1.length() || v1.charAt(to1) == '+')
                return v2 + "\n";

            // zavrsio pre-release dio od v2, a dosad su jednake
            if(to2 == v2.length() || v2.charAt(to2) == '+')
                return v1 + "\n";

            to1++; to2++;
        }
    }

    @GetMapping("/versions/next")
    public String nextVersion(@RequestParam(value = "v", defaultValue = "") String v, @RequestParam(value = "type", defaultValue = "") String type)
    {
        if(type.isEmpty() || v.isEmpty())
            return "Unesite sve potrebne parametre. \n";

        if(!type.equals("MAJOR") && !type.equals("MINOR") && !type.equals("PATCH"))
        {
            return "Pogresan type. Moze biti: MAJOR, MINOR ili PATCH. \n";
        }

        String regex = "0\\.0\\.1(-(0|([1-9A-Za-z-][0-9A-Za-z-]*)|(0[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*))(\\.(0|([1-9A-Za-z-][0-9A-Za-z-]*)|(0[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*)))*)?(\\+[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(v);
        if(matcher.matches())
        {
            if(type.equals("MAJOR"))
                return "1.0.0  \n";
            else
                return "Računala sam da se s 0.0.1 može prijeći na 1.0.0 samo ako je ujedno type postavljen na MAJOR. \n"
                        + "Dakle, u ostalim slučajevima verzija 0.0.1 \"nije valjana\".\n";
        }

        regex = "^[1-9]\\d*\\.(0|([1-9]\\d*))\\.(0|([1-9]\\d*))(-(0|([1-9A-Za-z-][0-9A-Za-z-]*)|(0[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*))(\\.(0|([1-9A-Za-z-][0-9A-Za-z-]*)|(0[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*)))*)?(\\+[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?$";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(v);
        if(!matcher.matches())
        {
            return "Semanticka verzija nije valjana. Proucite detaljno dozvoljene oblike na: https://semver.org/spec/v2.0.0.html \n";
        }

        int major, minor, patch;

        int from = 0, to = 0;
        while(v.charAt(to) != '.')
            to++;
        major = Integer.parseInt(v.substring(from, to));

        to++;
        from = to;
        while(v.charAt(to) != '.')
            to++;
        minor = Integer.parseInt(v.substring(from, to));

        to++;
        from = to;
        while(to != v.length() && v.charAt(to) != '-' && v.charAt(to) != '+')
            to++;
        patch = Integer.parseInt(v.substring(from, to));

        if(type.equals("PATCH"))
        {
            patch++;
        }
        else if(type.equals("MINOR"))
        {
            minor++;
            patch = 0;
        }
        else
        {
            major++;
            minor = 0;
            patch = 0;
        }

        return Integer.toString(major) + "." + Integer.toString(minor) + "." + Integer.toString(patch) + "\n";
    }

    public boolean provjeraVerzije(String v)
    {
        String regex = "^0\\.0\\.1(-(0|([1-9A-Za-z-][0-9A-Za-z-]*)|(0[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*))(\\.(0|([1-9A-Za-z-][0-9A-Za-z-]*)|(0[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*)))*)?(\\+[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(v);
        if (!matcher.matches()) {
            // verzija nije 0.0.1

            regex = "^[1-9]\\d*\\.(0|([1-9]\\d*))\\.(0|([1-9]\\d*))(-(0|([1-9A-Za-z-][0-9A-Za-z-]*)|(0[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*))(\\.(0|([1-9A-Za-z-][0-9A-Za-z-]*)|(0[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*)))*)?(\\+[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?$";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(v);

            if (!matcher.matches())  // verzija nije valjana
                return false;
            else            // verzija je valjana
                return true;
        }

        // verzija je 0.0.1
        return true;
    }

}
