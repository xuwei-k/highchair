package highchair.datastore

import meta._
import collection.JavaConversions.asIterable
import com.google.appengine.api.datastore.{
  DatastoreService,
  FetchOptions,
  Query => GQuery
}

case class Query[E <: Entity[E], K <: Kind[E]](
  kind:     K,
  filters:  List[Filter[E, _]],
  sorts:    List[Sort[E, _]]) {
  
  def baseQuery = new GQuery(kind.reflector.simpleName)
  /** Construct a native datastore query. */
  def rawQuery = 
    (baseQuery /: (filters ::: sorts)) { (q, f) => f bind q }
  /** Add a filter. */
  def and(f: K => Filter[E, _]) =
    copy(filters = f(kind) :: filters)
  /** Sort ascending on some property. TODO moar types */
  def orderAsc(f: K => PropertyMapping[E, _]) =
    copy(sorts = Asc(f(kind)) :: sorts)
  /** Sort descending on some property. TODO moar types */
  def orderDesc(f: K => PropertyMapping[E, _]) =
    copy(sorts = Desc(f(kind)) :: sorts)
  /**
   * Generate a raw GQL query as a string. This requires an active API
   * environment registered on the current thread.
   */
  def toGQLString() = rawQuery.toString
  
  /** Fetch a single record matching this query. */
  def fetchOne()(implicit dss: DatastoreService) = 
    fetch() headOption
  /** Fetch only the keys of entities matching this query. More efficient. */
  def fetchKeys()(implicit dss: DatastoreService) = {
    val q = rawQuery setKeysOnly()
    asIterable(dss.prepare(q).asIterable) map (_ getKey)
  }
  /** Fetch entities matching this query, optionally providing limits and/or offsets. */
  // TODO better default; clean up; what happens when offset extends the bounds?
  def fetch(limit: Int = 500, skip: Int = 0)(implicit dss: DatastoreService) = {
    val opts = FetchOptions.Builder withOffset(skip) limit(limit)
    asIterable(dss.prepare(rawQuery).asIterable(opts)) map kind.entity2Object
  }
}
