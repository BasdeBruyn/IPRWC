export class User {
  constructor(public email: string,
              public password: string,
              public isAdmin: boolean,
              public id = -1) {}
}
