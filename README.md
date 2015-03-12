This is a simple reagent-based project to generate a playlist based on
foursquare checkin data.

The main code is based on the excellent 
[chestnut template](https://github.com/plexus/chestnut), but it uses 
[reagent](https://github.com/reagent-project/reagent), 
[re-frame](https://github.com/Day8/re-frame) and 
[kioo](https://github.com/ckirkendall/kioo) rather than Om.

**Note:** kioo currently has an incompatibility with reagent 0.5.0 which 
is fixed on master but hasn't been released yet, so to get this working 
as of this moment, you'll need to clone kioo's repo and run `lein install` 
on it to gt it into your local repository. 
([cf](https://github.com/ckirkendall/kioo/issues/44))

See the chestnut page for details on getting it running, but the 
synopsis is:

* `lein repl`
* `(run)`
* `(browser-repl)` if you want (figwheel will reload compiled js as you save it)

### Todo

* Store token in localstorage
* Also cache api results in there, both with low ttl
* Use [this cljs fork](https://github.com/quile/component-cljs) of `stuartsierra/component` 
to handle routing etc.
* Twitter data source
* Use echonest or similarly agnostic song metadata source
