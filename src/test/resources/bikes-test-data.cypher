CREATE
  (bike1:Bike{
    externalId:'Roubaix'
  }),
  (bikeState1:BikeState{
    externalId:'Roubaix',
    name:"Robert's Roubaix",
    description:'2010 Roubaix with Tiagra and Fulcrum Racing 4s',
    from: localdatetime('2020-06-08T08:52:20.123456'),
    to: localdatetime('9999-12-31T23:59:59.999')
  }),
  (previousBikeState1:BikeState{
    externalId:'Roubaix',
    name:"Robert's Roubaix",
    description:'2010 Roubaix with Tiagra and Fulcrum Racing 4s - previous',
    from: localdatetime('2020-06-07T08:52:20.123456'),
    to: localdatetime('2020-06-08T08:52:20.123456')
  }),
  (bike1)-[:CURRENT_STATE]->(bikeState1),
  (bike1)-[:BIKE_STATE]->(bikeState1),
  (bike1)-[:BIKE_STATE]->(previousBikeState1),
  (bikeState1)-[:PREVIOUS_STATE]->(previousBikeState1)
CREATE
  (bike2:Bike{
    externalId:'Emonda'
  }),
  (bikeState2:BikeState{
    externalId:'Emonda',
    name:"Robert's Emonda",
    description:'2016 Trek Émonda frame with Shimano Tiagra 4700 and Fulcrum Racing 4s',
    from: localdatetime('2020-07-30T19:14:00'),
    to: localdatetime('9999-12-31T23:59:59.999')
  }),
  (bike2)-[:CURRENT_STATE]->(bikeState2),
  (bike2)-[:BIKE_STATE]->(bikeState2)
CREATE
  (wheel:Wheel{
    externalId:'Fulcrum4'
  })-[:CURRENT_STATE]->
  (wheelState:WheelState{
    externalId:'Fulcrum4',
    name:'Fulcrum Racing 4',
    description:'2019 Fulcrum Racing 4s with GP5000s',
    from: localdatetime('2020-06-08T08:52:20.123456'),
    to: localdatetime('9999-12-31T23:59:59.999')
  }),
  (wheel)-[:WHEEL_STATE]->(wheelState),
  (bikeState1)-[:HAS_COMPONENT]->(wheelState),
  (bikeState2)-[:HAS_COMPONENT]->(wheelState)